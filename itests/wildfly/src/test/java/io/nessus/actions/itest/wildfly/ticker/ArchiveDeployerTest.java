/*-
 * #%L
 * Nessus :: Weka :: API
 * %%
 * Copyright (C) 2020 Nessus
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.nessus.actions.itest.wildfly.ticker;


import static io.nessus.actions.model.Model.CAMEL_ACTIONS_RESOURCE_NAME;

import java.math.BigDecimal;

import org.jboss.as.controller.client.ModelControllerClientConfiguration;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.nessus.actions.itest.wildfly.ticker.sub.ApplicationScopedRouteBuilder;
import io.nessus.actions.model.Model;
import io.nessus.actions.model.converters.TickerTypeConverters;
import io.nessus.actions.runner.ModelBasedRouteBuilder;
import io.nessus.actions.runner.RemoteArchiveDeployer;
import io.nessus.actions.testing.AbstractActionsTest;
import io.nessus.actions.testing.HttpRequest;
import io.nessus.actions.testing.HttpRequest.HttpResponse;

public class ArchiveDeployerTest extends AbstractActionsTest {
        
	static final String DEPLOYMENT_NAME = "crypto-ticker.war";
	
    public static WebArchive createDeployment() {
    	WebArchive archive = ShrinkWrap.create(WebArchive.class, DEPLOYMENT_NAME);
    	archive.addPackage(Model.class.getPackage());
    	archive.addPackage(ModelBasedRouteBuilder.class.getPackage());
    	archive.addClasses(ApplicationScopedRouteBuilder.class, TickerTypeConverters.class);
    	archive.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        archive.addAsWebInfResource("ticker/jboss-deployment-structure.xml", "jboss-deployment-structure.xml");
        archive.addAsResource("ticker/crypto-ticker.yml", CAMEL_ACTIONS_RESOURCE_NAME);
        return archive;
    }
    
    @Test
    public void warDeployment() throws Exception {

		ModelControllerClientConfiguration.Builder clientConfigBuilder = new ModelControllerClientConfiguration.Builder()
                .setHandler(getUsernamePasswordHandler())
                .setProtocol("remote+http")
                .setHostName("127.0.0.1")
                .setPort(9990);

        ModelControllerClientConfiguration clientConfig = clientConfigBuilder.build();
        try (RemoteArchiveDeployer deployer = new RemoteArchiveDeployer(clientConfig)) {
        	
            LOG.warn("WebUri: {}", deployer.getWebUri());
            LOG.warn("Running: {}", deployer.isServerInRunningState());
            
            String pair = "BTC/USDT";
            String httpUrl = "http://127.0.0.1:8080/ticker?currencyPair=" + pair;

            HttpResponse res = HttpRequest.get(httpUrl).getResponse();
            Assert.assertEquals(404, res.getStatusCode());
    		
            WebArchive archive = createDeployment();
            String runtimeName = deployer.deploy(archive);
            try {
            	
        		res = HttpRequest.get(httpUrl).getResponse();
                Assert.assertEquals(200, res.getStatusCode());
        		
                LOG.info(res.getBody());
                
                ObjectMapper mapper = new ObjectMapper();
                BigDecimal closePrice = mapper.readTree(res.getBody()).at("/last").decimalValue();
                LOG.info(String.format("%s: %.2f", pair, closePrice));
                
            } finally {
    			deployer.undeploy(runtimeName);
    		}

    		res = HttpRequest.get(httpUrl).getResponse();
            Assert.assertEquals(404, res.getStatusCode());
        }
    }
}

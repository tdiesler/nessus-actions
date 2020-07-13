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


import java.math.BigDecimal;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.nessus.actions.itest.wildfly.ticker.sub.ApplicationScopedRouteBuilder;
import io.nessus.actions.itest.wildfly.ticker.sub.TickerTypeConverters;
import io.nessus.actions.testing.AbstractActionsTest;
import io.nessus.actions.testing.HttpRequest;
import io.nessus.actions.testing.HttpRequest.HttpResponse;

@RunAsClient
@RunWith(Arquillian.class)
public class TickerDeploymentTest extends AbstractActionsTest {
    
	static final String DEPLOYMENT_NAME = "crypto-ticker";
	
    @Deployment(name = DEPLOYMENT_NAME, testable = false)
    public static WebArchive createdeployment() {
    	WebArchive archive = ShrinkWrap.create(WebArchive.class, "crypto-ticker-undertow.war");
    	archive.addClasses(ApplicationScopedRouteBuilder.class, TickerTypeConverters.class);
    	archive.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        return archive;
    }
    
    @Test
    public void testWithNessusActions() throws Exception {

        String httpUrl = "http://127.0.0.1:8080/ticker";
        
        String pair = "BTC/USDT";
		HttpResponse res = HttpRequest.get(httpUrl + "?currencyPair=" + pair).getResponse();
        Assert.assertEquals(200, res.getStatusCode());
		
        LOG.info(res.getBody());
        
        ObjectMapper mapper = new ObjectMapper();
        BigDecimal closePrice = mapper.readTree(res.getBody()).at("/last").decimalValue();
        LOG.info(String.format("%s: %.2f", pair, closePrice));
    }
}

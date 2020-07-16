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

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spi.TypeConverterRegistry;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.extension.camel.CamelAware;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.nessus.actions.model.Model;
import io.nessus.actions.model.utils.TickerTypeConverters;
import io.nessus.actions.testing.AbstractActionsTest;
import io.nessus.actions.testing.HttpRequest;
import io.nessus.actions.testing.HttpRequest.HttpResponse;

@CamelAware
@RunWith(Arquillian.class)
public class TickerIntegrationTest extends AbstractActionsTest {
    
    @Deployment
    public static WebArchive createdeployment() {
    	WebArchive archive = ShrinkWrap.create(WebArchive.class, "crypto-ticker.war");
        archive.addPackage(AbstractActionsTest.class.getPackage());
        archive.addPackages(true, Model.class.getPackage());
        archive.addAsWebInfResource("ticker/jboss-deployment-structure.xml", "jboss-deployment-structure.xml");
        archive.addAsResource("ticker/crypto-ticker.yml", CAMEL_ACTIONS_RESOURCE_NAME);
        return archive;
    }

    @Test
    public void testWithPlainCamel() throws Exception {
    	
        String httpUrl = "http://127.0.0.1:8080/ticker";
        
        try (CamelContext camelctx = new DefaultCamelContext()) {
        	
    		TypeConverterRegistry registry = camelctx.getTypeConverterRegistry();
    		registry.addTypeConverters(new TickerTypeConverters());
        	
    		camelctx.addRoutes(new RouteBuilder() {
                @Override
                public void configure() {
					fromF("undertow:" + httpUrl)
                    	.to("xchange:binance?service=marketdata&method=ticker&currencyPair=BTC/USDT")
                    	.marshal().json(JsonLibrary.Jackson, true);
                }
            });

    		camelctx.start();
    		
    		HttpResponse res = HttpRequest.get(httpUrl).getResponse();
            Assert.assertEquals(200, res.getStatusCode());
    		
            LOG.info(res.getBody());
            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode resTree = mapper.readTree(res.getBody());
            
			String pair = resTree.at("/currencyPair").asText();
			BigDecimal closePrice = resTree.at("/last").decimalValue();
            LOG.info(String.format("%s: %.2f", pair, closePrice));
        }
    }
}

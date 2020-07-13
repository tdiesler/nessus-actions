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
package io.nessus.actions.test.runner;


import java.math.BigDecimal;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spi.TypeConverterRegistry;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.nessus.actions.model.Model;
import io.nessus.actions.runner.StandaloneRunner;
import io.nessus.actions.testing.AbstractActionsTest;
import io.nessus.actions.testing.HttpRequest;
import io.nessus.actions.testing.HttpRequest.HttpResponse;

public class StandaloneRunnerTest extends AbstractActionsTest {
    
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
                    	.to("xchange:binance?service=marketdata&method=ticker")
                    	.marshal().json(JsonLibrary.Jackson, true);
                }
            });

    		camelctx.start();
    		
    		String pair = "BTC/USDT";
    		HttpResponse res = HttpRequest.get(httpUrl + "?currencyPair=" + pair).getResponse();
            Assert.assertEquals(200, res.getStatusCode());
    		
            LOG.info(res.getBody());
            
            ObjectMapper mapper = new ObjectMapper();
            BigDecimal closePrice = mapper.readTree(res.getBody()).at("/last").decimalValue();
            LOG.info(String.format("%s: %.2f", pair, closePrice));
        }
    }
    
    @Test
    public void testWithNessusActions() throws Exception {
    	
        Model model = getModelFromResource("/crypto-ticker.yml");
        String httpUrl = model.getFrom().getWith();
    	
    	try (StandaloneRunner runner = new StandaloneRunner(model)) {
    		
    		runner.addTypeConverters(new TickerTypeConverters());
    		
        	runner.start();

    		String pair = "BTC/USDT";
    		HttpResponse res = HttpRequest.get(httpUrl + "?currencyPair=" + pair).getResponse();
            Assert.assertEquals(200, res.getStatusCode());
    		
            LOG.info(res.getBody());
            
            ObjectMapper mapper = new ObjectMapper();
            BigDecimal closePrice = mapper.readTree(res.getBody()).at("/last").decimalValue();
            LOG.info(String.format("%s: %.2f", pair, closePrice));
    	}
    }
    
}

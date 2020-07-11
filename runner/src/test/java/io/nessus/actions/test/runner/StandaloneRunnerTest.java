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

import org.junit.Assert;
import org.junit.Test;

import io.nessus.actions.model.Model;
import io.nessus.actions.runner.StandaloneRunner;
import io.nessus.actions.testing.AbstractActionsTest;
import io.nessus.actions.testing.AvailablePortFinder;
import io.nessus.actions.testing.HttpRequest;
import io.nessus.actions.testing.HttpRequest.HttpResponse;

public class StandaloneRunnerTest extends AbstractActionsTest {
    
    @Test
    public void runStandalone() throws Exception {
    	
		int port = AvailablePortFinder.getNextAvailable(8080);
		String fromUrl = String.format("http://127.0.0.1:%d/ticker", port);
		
    	Model model = new Model.Builder()
    			.from("camel/undertow@v1", fromUrl)
    			.to("camel/xchange@v1", "binance?service=marketdata&method=ticker")
    			.build();
    	
    	try (StandaloneRunner runner = new StandaloneRunner(model)) {
    		
    		runner.addTypeConverters(new TickerTypeConverters());
    		
        	runner.start();

    		String pair = "BTC/USDT";
    		HttpResponse res = HttpRequest.get(fromUrl + "?currencyPair=" + pair).getResponse();
            Assert.assertEquals(200, res.getStatusCode());
    		
            BigDecimal closePrice = new BigDecimal(res.getBody());
            LOG.info(String.format("%s: %.2f", pair, closePrice));
    	}
    }
    
}

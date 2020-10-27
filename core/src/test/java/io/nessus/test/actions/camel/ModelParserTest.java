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
package io.nessus.test.actions.camel;


import org.junit.Assert;
import org.junit.Test;

import io.nessus.actions.core.model.FromStep;
import io.nessus.actions.core.model.MarshalStep;
import io.nessus.actions.core.model.RouteModel;
import io.nessus.actions.core.model.ToStep;
import io.nessus.actions.core.model.RouteModel.TargetRuntime;
import io.nessus.common.BasicConfig;
import io.nessus.common.testing.AbstractTest;

public class ModelParserTest extends AbstractTest<BasicConfig> {
    
    @Test
    public void writeModel() throws Exception {
    	
		RouteModel expModel = new RouteModel("Crypto Ticker", TargetRuntime.eap)
				.withStep(new FromStep("camel/undertow@v1", "http://0.0.0.0:8080/ticker")
					.withParams("{currencyPair=BTC/USDT}"))
				.withStep(new ToStep("camel/xchange@v1", "binance")
					.withParams("{service=marketdata, method=ticker}"))
				.withStep(new MarshalStep("json", true));
    			
        String expTxt = expModel.toString();
		logInfo(expTxt);
		
		RouteModel wasModel = RouteModel.read(expTxt);
		String wasTxt = wasModel.toString();
		logInfo(wasTxt);
		
		Assert.assertEquals(expTxt, wasTxt);
    }
}

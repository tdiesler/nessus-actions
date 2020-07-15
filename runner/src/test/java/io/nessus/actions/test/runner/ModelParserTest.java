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


import org.junit.Assert;
import org.junit.Test;

import io.nessus.actions.model.Model;
import io.nessus.actions.model.ModelBuilder;
import io.nessus.actions.testing.AbstractActionsTest;
import io.nessus.actions.model.Model.Runtime;

public class ModelParserTest extends AbstractActionsTest {
    
    @Test
    public void parseModel() throws Exception {
    	
        Model model = getModelFromResource("/crypto-ticker.yml");
        LOG.info(model.toString());
    }

    @Test
    public void writeModel() throws Exception {
    	
    	Model was = new ModelBuilder("Crypto Ticker")
    			.runtime(Runtime.standalone)
    			.from("camel/undertow@v1")
					.with("http://127.0.0.1:8080/ticker")
					.build()
    			.to("camel/xchange@v1")
					.with("binance")
					.param("service", "marketdata")
					.param("method", "ticker")
					.build()
				.marshall("json", true)
    			.build();
    			
        LOG.info(was.toString());
        
        Model exp = getModelFromResource("/crypto-ticker.yml");
        Assert.assertEquals(exp.toString(), was.toString());
    }
}

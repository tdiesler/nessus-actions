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
package io.nessus.test.actions.core;


import org.junit.Assert;
import org.junit.Test;

import io.nessus.actions.core.utils.ApiUtils;

public class IdentifierTest extends AbstractCoreTest {
    
    @Test
    public void testDefault() throws Exception {
    	
    	for (int i = 0; i < 12; i++) {
        	String id = ApiUtils.createIdentifier(4);
        	Assert.assertEquals(4 * 6 + 3, id.length());
        	logInfo("{}", id);
    	}
    }
    
    @Test
    public void testForUser() throws Exception {
    	
    	String userId = "d035fe57-55e7-4631-ba09-4d5a0c075d3b";
    	for (int i = 0; i < 12; i++) {
        	String id = ApiUtils.createIdentifier(userId);
        	Assert.assertTrue(id.startsWith("d035fe57-"));
        	logInfo("{}", id);
    	}
    }
}

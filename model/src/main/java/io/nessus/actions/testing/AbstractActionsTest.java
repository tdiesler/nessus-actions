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
package io.nessus.actions.testing;


import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nessus.actions.model.AssertState;
import io.nessus.actions.model.Model;
import io.nessus.actions.model.UsernamePasswordHandler;

public abstract class AbstractActionsTest  {
	
	protected final Logger LOG = LoggerFactory.getLogger(getClass());

	protected Model getModelFromResource(String resource) throws IOException {
		
		URL input = getClass().getResource(resource);
		if (input == null && !resource.startsWith("/")) 
			input = getClass().getResource("/" + resource);
		
		AssertState.notNull(input, "Cannot find: " + resource);
		
		Model model = Model.read(input);
		return model;
	}

	protected String getServerUsername() {
		return System.getProperty("server.username");
	}

	protected char[] getServerPassword() {
		String password = System.getProperty("server.password");
		return password != null ? password.toCharArray() : null;
	}
    
	protected UsernamePasswordHandler getUsernamePasswordHandler() {
		return new UsernamePasswordHandler(getServerUsername(), getServerPassword());
	}
}

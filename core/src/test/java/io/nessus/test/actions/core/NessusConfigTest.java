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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.nessus.actions.core.NessusConfig;
import io.nessus.common.testing.AbstractTest;

public class NessusConfigTest extends AbstractTest<NessusConfig> {

	@Test
	public void testConfig() throws Exception {

		NessusConfig config = NessusConfig.createConfig();
		
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
		String yaml = writer.writeValueAsString(config);
		
		logInfo(yaml);

		Assert.assertEquals("http://127.0.0.1:8000/auth", config.getKeycloakUrl());
		Assert.assertEquals("http://127.0.0.1:8100/maven", config.getMavenUrl());
		Assert.assertEquals("http://127.0.0.1:8200/jaxrs", config.getJaxrsUrl());
		Assert.assertEquals("http://127.0.0.1:8300/portal", config.getPortalUrl());
		Assert.assertEquals("myrealm", config.getKeycloakRealmId());
		Assert.assertEquals("myclient", config.getKeycloakClientId());
	}
}
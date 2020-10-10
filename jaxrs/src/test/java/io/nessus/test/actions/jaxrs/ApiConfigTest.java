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
package io.nessus.test.actions.jaxrs;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.nessus.actions.jaxrs.ApiConfig;
import io.nessus.actions.jaxrs.ApiRoot;
import io.nessus.actions.jaxrs.service.ApiService;

public class ApiConfigTest extends AbstractApiTest {

	@Test
	public void testConfig() throws Exception {

		ApiConfig config = ApiConfig.createConfig();
		
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
		String yaml = writer.writeValueAsString(config);
		
		logInfo(yaml);

		Assert.assertEquals("http://127.0.0.1:8280/tryit", config.getPortalUrl());
		Assert.assertEquals("http://127.0.0.1:8180/auth", config.getKeycloakUrl());
		Assert.assertEquals("myrealm", config.getRealmId());
		Assert.assertEquals("myclient", config.getClientId());
	}

	@Test
	public void testMasterAccessToken() throws Exception {

		ApiRoot api = ApiRoot.getInstance();
		ApiService apisrv = api.getApiService();

		String masterAccessToken = apisrv.getMasterAccessToken();
		Assert.assertNotNull(masterAccessToken);
		
		ApiConfig config = api.getConfig();
		String refreshToken = config.getMasterRefreshToken();
		Assert.assertNotNull(refreshToken);
	}
}
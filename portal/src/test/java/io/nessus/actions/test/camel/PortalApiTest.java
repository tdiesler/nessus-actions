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
package io.nessus.actions.test.camel;

import static io.nessus.actions.portal.ApiUtils.getStatus;
import static io.nessus.actions.portal.ApiUtils.hasStatus;
import static io.nessus.actions.portal.ApiUtils.keycloakRealmURL;
import static io.nessus.actions.portal.ApiUtils.portalURL;

import java.net.URL;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.nessus.actions.portal.ApiUtils;
import io.nessus.actions.portal.Config;
import io.nessus.actions.portal.resources.PortalApi;
import io.nessus.actions.portal.resources.PortalStatus.StatusResponse;
import io.nessus.actions.portal.resources.UserRegister.User;

public class PortalApiTest extends AbstractApiTest {

	@Test
	public void testStatus() throws Exception {
		
		Response res = client
			.target(portalURL("/api/status"))
			.request().get();
		
		Assert.assertEquals(Status.OK, getStatus(res));
		
		StatusResponse status = res.readEntity(StatusResponse.class);
		Assert.assertEquals("online", status.getStatus());
	}

	@Test
	public void testUserRegister() throws Exception {

		URL resUrl = getClass().getResource("/json/user-register.json");
		
		ObjectMapper mapper = new ObjectMapper();
		User user = mapper.readValue(resUrl, User.class);
		Assert.assertEquals("myuser@example.com", user.getEmail());
		
		Response res = client
			.target(portalURL("/api/users"))
			.request().post(Entity.json(user));
		
		Status status = getStatus(res);
		Assert.assertTrue(Status.CREATED == status || Status.CONFLICT == status);
	}

	@Test
	public void testUserInfo() throws Exception {

		Config cfg = Config.getInstance();
		PortalApi api = PortalApi.getInstance();
		
		String token = api.getAccessToken(cfg.getRealmId(), cfg.getClientId(), cfg.getClientSecret(), cfg.getUsername(), cfg.getPassword());
		Assert.assertNotNull("token not null", token);
		
		String url = keycloakRealmURL(cfg.getRealmId(), "/protocol/openid-connect/userinfo");
		
		Response res = ClientBuilder.newClient().target(url)
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token)
				.get();
		
		hasStatus(Status.OK, res);
		
		JsonNode jnode = ApiUtils.readJsonNode(res);
		Assert.assertEquals("myuser@example.com", jnode.findPath("email").asText());
	}
}
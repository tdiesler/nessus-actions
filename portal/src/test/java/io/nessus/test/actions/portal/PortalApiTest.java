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
package io.nessus.test.actions.portal;

import static io.nessus.actions.portal.ApiUtils.getStatus;
import static io.nessus.actions.portal.ApiUtils.portalUrl;

import java.net.URL;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.nessus.actions.portal.resources.ApiService;
import io.nessus.actions.portal.resources.PortalStatus.StatusResponse;
import io.nessus.actions.portal.resources.UserRegister.User;

public class PortalApiTest extends AbstractPortalTest {

	@Test
	public void testClientSecret() throws Exception {
		
		String realmId = getConfig().getRealmId();
		String clientId = getConfig().getClientId();
		
		ApiService apisrv = getService(ApiService.class);
		String clientSecret = apisrv.getClientSecret(realmId, clientId);
		Assert.assertNotNull(clientSecret);
	}

	@Test
	public void testUserRegister() throws Exception {

		URL resUrl = getClass().getResource("/json/user-register.json");
		
		ObjectMapper mapper = new ObjectMapper();
		User user = mapper.readValue(resUrl, User.class);
		Assert.assertEquals("myuser@example.com", user.getEmail());
		
		Response res = ClientBuilder.newClient()
			.target(portalUrl("/api/users"))
			.request().post(Entity.json(user));
		
		assertStatus(res, Status.CREATED, Status.CONFLICT);
		
		MultivaluedMap<String, Object> headers = res.getHeaders();
		headers.entrySet().forEach((en) -> logInfo("{}: {}", en.getKey(), en.getValue()));
	}

	@Test
	public void testUserInfo() throws Exception {
		
	}

	@Test
	public void testUserStatus() throws Exception {
		
		Response res = ClientBuilder.newClient()
			.target(portalUrl("/api/status"))
			.request().get();
		
		Assert.assertEquals(Status.OK, getStatus(res));
		
		StatusResponse status = res.readEntity(StatusResponse.class);
		Assert.assertEquals("online", status.getStatus());
	}

}
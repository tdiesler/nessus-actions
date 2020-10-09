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

import static io.nessus.actions.jaxrs.ApiUtils.portalUrl;

import java.net.URL;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.nessus.actions.jaxrs.service.ApiService;
import io.nessus.actions.jaxrs.type.KeycloakTokens;
import io.nessus.actions.jaxrs.type.User;
import io.nessus.actions.jaxrs.type.UserInfo;

public class ApiSmokeTest extends AbstractApiTest {

	@Test
	public void testUserLifecycle() throws Exception {

		URL resUrl = getClass().getResource("/json/user-register.json");
		
		ObjectMapper mapper = new ObjectMapper();
		User user = mapper.readValue(resUrl, User.class);
		Assert.assertEquals("myuser@example.com", user.getEmail());
		
		// Register
		
		// POST http://localhost:8280/tryit/api/users
		// 
		// {
		//	  "firstName": "My",
		//	  "lastName":  "User", 
		//	  "email":	   "myuser@example.com",
		//	  "username":  "myuser", 
		//	  "password":  "mypass"
		// }
		
		Response res = withClient(portalUrl("/api/users"), 
				target -> target.request().post(Entity.json(user)));
		
		assertStatus(res, Status.CREATED, Status.CONFLICT);
		
		// Login
		
		// POST http://localhost:8280/tryit/api/user/token
		// Content-Type: application/x-www-form-urlencoded
		//
		// username: myuser 
		// password: mypass
		
		MultivaluedMap<String, String> reqmap = new MultivaluedHashMap<>();
		reqmap.add("username", user.getUsername());
		reqmap.add("password", user.getPassword());
		
		res = withClient(portalUrl("/api/user/token"), 
				target -> target.request().post(Entity.form(reqmap)));
		
		assertStatus(res, Status.OK);
		
		KeycloakTokens tokens = res.readEntity(KeycloakTokens.class);
		
		// Status
		
		// GET http://localhost:8280/tryit/api/user/status
		// Authorization: "Bearer eyJhbGciOi..."
		
		ApiService apisrv = getService(ApiService.class);
		String accessToken = apisrv.refreshAccessToken(tokens.refreshToken);
		Assert.assertNotNull("Null access token", accessToken);
		
		res = withClient(portalUrl("/api/user/status"), 
				target -> target.request()
					.header("Authorization", "Bearer " + accessToken)
					.get());
		
		assertStatus(res, Status.OK);
		
		UserInfo userInfo = res.readEntity(UserInfo.class);
		Assert.assertEquals("myuser@example.com", userInfo.getEmail());
		
		// Delete
		
		// DELETE http://localhost:8280/tryit/api/user
		// Authorization: "Bearer eyJhbGciOi..."
		
		res = withClient(portalUrl("/api/user"), 
				target -> target.request()
					.header("Authorization", "Bearer " + accessToken)
					.delete());
		
		assertStatus(res, Status.NO_CONTENT);
	}
}
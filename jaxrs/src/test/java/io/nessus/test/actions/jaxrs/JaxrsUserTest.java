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

import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.nessus.actions.core.service.KeycloakService;
import io.nessus.actions.jaxrs.type.UserRegister;
import io.nessus.actions.jaxrs.type.UserState;
import io.nessus.actions.jaxrs.type.UserTokens;
import io.nessus.common.rest.SSLContextBuilder;

public class JaxrsUserTest extends AbstractJaxrsTest {

	@Before
	public void before() throws Exception {
		super.before();
		
		if (isUseTLS()) {
			
			Path srcdir = Paths.get("src/test/resources/tls-public");
			Path pemPath = srcdir.resolve("tls.pem");
			
			SSLContext sslContext = new SSLContextBuilder()
					.keystorePath(Paths.get("target/keystore-public.jks"))
					.addPem("self-signed-public", pemPath)
					.build();
			
			SSLContext.setDefault(sslContext);
			
			getConfig().setUseTLS(true);
		}
	}
	
	@Test
	public void testUserLifecycle() throws Exception {

		URL resUrl = getClass().getResource("/json/user-register.json");
		
		ObjectMapper mapper = new ObjectMapper();
		UserRegister user = mapper.readValue(resUrl, UserRegister.class);
		Assert.assertEquals("myuser@example.com", user.getEmail());
		
		// User Register
		
		// PUT http://localhost:8200/jaxrs/api/users
		// 
		// {
		//	  "firstName": "My",
		//	  "lastName":  "User", 
		//	  "email":	   "myuser@example.com",
		//	  "username":  "myuser", 
		//	  "password":  "mypass"
		// }
		
		URI uri = jaxrsUri("/api/users");
		Response res = withClient(uri, target -> target.request()
				.put(Entity.json(user)));
		
		assertStatus(res, Status.CREATED, Status.CONFLICT);
		
		// User Login
		
		// POST http://localhost:8200/jaxrs/api/user/token
		// Content-Type: application/x-www-form-urlencoded
		//
		// username: myuser 
		// password: mypass
		
		MultivaluedMap<String, String> data = new MultivaluedHashMap<>();
		data.add("username", user.getUsername());
		data.add("password", user.getPassword());
		
		uri = jaxrsUri("/api/users/login");
		res = withClient(uri, target -> target.request()
				.post(Entity.form(data)));
		
		assertStatus(res, Status.OK);
		
		UserTokens tokens = res.readEntity(UserTokens.class);
		String refreshToken = tokens.refreshToken;
		String userId = tokens.userId;
		
		// User State
		
		// GET http://localhost:8200/jaxrs/api/user/state
		// Authorization: "Bearer eyJhbGciOi..."
		
		KeycloakService kcsrv = getService(KeycloakService.class);
		String accessToken = kcsrv.refreshAccessToken(refreshToken);
		Assert.assertNotNull("Null access token", accessToken);
		
		uri = jaxrsUri("/api/user/" + userId + "/state");
		res = withClient(uri, target -> target.request()
					.header("Authorization", "Bearer " + accessToken)
					.get());
		
		assertStatus(res, Status.OK);
		
		UserState userInfo = res.readEntity(UserState.class);
		Assert.assertEquals("myuser@example.com", userInfo.getEmail());
		
		// User Delete
		
		// DELETE http://localhost:8200/jaxrs/api/user
		// Authorization: "Bearer eyJhbGciOi..."
		
		uri = jaxrsUri("/api/user/" + userId);
		res = withClient(uri, target -> target.request()
					.header("Authorization", "Bearer " + accessToken)
					.delete());
		
		assertStatus(res, Status.NO_CONTENT);
	}

	private boolean isUseTLS() {
		
		boolean useTLS = getConfig().isUseTLS();
		if (useTLS) return true; 
		
		String sysprop = System.getProperty("useTLS");
		if (!useTLS && sysprop != null) {
			useTLS = sysprop.length() > 0 ? Boolean.valueOf(sysprop) : true;
		}
		
		return useTLS;
	}
}
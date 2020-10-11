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

import static io.nessus.actions.jaxrs.utils.JaxrsUtils.jaxrsUrl;

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

import io.nessus.actions.jaxrs.service.KeycloakService;
import io.nessus.actions.jaxrs.type.KeycloakTokens;
import io.nessus.actions.jaxrs.type.User;
import io.nessus.actions.jaxrs.type.UserInfo;
import io.nessus.actions.jaxrs.utils.KeycloakUtils;
import io.nessus.actions.jaxrs.utils.SSLContextBuilder;

public class JaxrsSmokeTest extends AbstractApiTest {

	boolean useTLS = false;
	
	@Before
	public void before() throws Exception {
		super.before();
		
		if (useTLS) {
			
			Path srcdir = Paths.get("src/test/resources/tls-public");
			Path pemPath = srcdir.resolve("tls.pem");
			
			SSLContext sslContext = new SSLContextBuilder()
					.keystorePath(Paths.get("target/keystore-public.jks"))
					.addPem("self-signed-public", pemPath)
					.build();
			
			SSLContext.setDefault(sslContext);
		}
	}
	
	/*
	 * https://access.redhat.com/solutions/973783
	 * 
	 * -Djavax.net.debug=ssl,handshake
	 * -Djavax.net.debug=all
	 */
	@Test
	public void testPublicTLS() throws Exception {
		
		MultivaluedHashMap<String, String> data = new MultivaluedHashMap<>();
		data.add("client_id", "admin-cli");
		data.add("username", getConfig().getMasterUser());
		data.add("password", getConfig().getMasterPassword());
		data.add("grant_type", "password");
		
		String url = KeycloakUtils.keycloakUrl("/realms/master/protocol/openid-connect/token", useTLS);
		
		Response res = withClient(url, target -> target.request().post(Entity.form(data)));
		
		assertStatus(res, Status.OK);
	}
	
	@Test
	public void testUserLifecycle() throws Exception {

		URL resUrl = getClass().getResource("/json/user-register.json");
		
		ObjectMapper mapper = new ObjectMapper();
		User user = mapper.readValue(resUrl, User.class);
		Assert.assertEquals("myuser@example.com", user.getEmail());
		
		// Register
		
		// POST http://localhost:7080/tryit/api/users
		// 
		// {
		//	  "firstName": "My",
		//	  "lastName":  "User", 
		//	  "email":	   "myuser@example.com",
		//	  "username":  "myuser", 
		//	  "password":  "mypass"
		// }
		
		Response res = withClient(jaxrsUrl("/api/users", useTLS), 
				target -> target.request().post(Entity.json(user)));
		
		assertStatus(res, Status.CREATED, Status.CONFLICT);
		
		// Login
		
		// POST http://localhost:7080/tryit/api/user/token
		// Content-Type: application/x-www-form-urlencoded
		//
		// username: myuser 
		// password: mypass
		
		MultivaluedMap<String, String> data = new MultivaluedHashMap<>();
		data.add("username", user.getUsername());
		data.add("password", user.getPassword());
		
		res = withClient(jaxrsUrl("/api/user/token", useTLS), 
				target -> target.request().post(Entity.form(data)));
		
		assertStatus(res, Status.OK);
		
		KeycloakTokens tokens = res.readEntity(KeycloakTokens.class);
		
		// Status
		
		// GET http://localhost:7080/tryit/api/user/status
		// Authorization: "Bearer eyJhbGciOi..."
		
		KeycloakService apisrv = getService(KeycloakService.class);
		String accessToken = apisrv.refreshAccessToken(tokens.refreshToken);
		Assert.assertNotNull("Null access token", accessToken);
		
		res = withClient(jaxrsUrl("/api/user/status", useTLS), 
				target -> target.request()
					.header("Authorization", "Bearer " + accessToken)
					.get());
		
		assertStatus(res, Status.OK);
		
		UserInfo userInfo = res.readEntity(UserInfo.class);
		Assert.assertEquals("myuser@example.com", userInfo.getEmail());
		
		// Delete
		
		// DELETE http://localhost:7080/tryit/api/user
		// Authorization: "Bearer eyJhbGciOi..."
		
		res = withClient(jaxrsUrl("/api/user", useTLS), 
				target -> target.request()
					.header("Authorization", "Bearer " + accessToken)
					.delete());
		
		assertStatus(res, Status.NO_CONTENT);
	}
}
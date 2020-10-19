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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.nessus.actions.jaxrs.type.UserModel;
import io.nessus.actions.jaxrs.type.UserModelAdd;
import io.nessus.actions.jaxrs.type.UserModels;
import io.nessus.actions.jaxrs.type.UserRegister;
import io.nessus.actions.jaxrs.type.UserTokens;
import io.nessus.actions.model.Model;

public class JaxrsModelTest extends AbstractJaxrsTest {

	@Test
	public void testModelLifecycle() throws Exception {

		// Register User
		
		UserTokens tokens = registerUser();
		
		String accessToken = tokens.accessToken;
		String userId = tokens.userId;
		
		InputStream input = getClass().getResourceAsStream("/model/crypto-ticker.yaml");
		Model model = Model.read(input);

		String title = model.getTitle();
		String content = model.toString();
		UserModelAdd modelAdd = new UserModelAdd(userId, title, content);
		
		// Create Model
		
		// PUT http://localhost:7080/jaxrs/api/user/{userId}/models
		// 
		// {
		//	  "userId": "myuser",
		//	  "content": "some model content", 
		// }

		String url = jaxrsUrl("/api/user/" + userId + "/models");
		Response res = withClient(url, target -> target
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + accessToken)
					.put(Entity.json(modelAdd)));
		
		assertStatus(res, Status.CREATED);
		
		UserModel userModel = res.readEntity(UserModel.class);
		String modelId = userModel.modelId;
		
		Assert.assertEquals(userId, userModel.userId);
		Assert.assertEquals(content, userModel.content);
		Assert.assertNotNull(modelId);
		
		// Get Models
		
		// GET http://localhost:7080/jaxrs/api/user/{userId}/models
		//
		
		res = withClient(url, target -> target.request()
				.header("Authorization", "Bearer " + accessToken)
				.get());
	
		assertStatus(res, Status.OK);
		
		UserModels userModels = res.readEntity(UserModels.class);
		Assert.assertEquals(userId, userModels.userId);
		Assert.assertEquals(modelId, userModels.models.get(0).modelId);
		
		// Get Model
		
		// GET http://localhost:7080/jaxrs/api/user/{userId}/model/{modelId}
		//
		
		url = jaxrsUrl("/api/user/" + userId + "/model/" + modelId);
		res = withClient(url, target -> target.request()
				.header("Authorization", "Bearer " + accessToken)
				.get());
	
		assertStatus(res, Status.OK);
		
		userModel = res.readEntity(UserModel.class);
		Assert.assertEquals(userId, userModel.userId);
		Assert.assertEquals(content, userModel.content);
		Assert.assertNotNull(modelId);

		// Update Model
		
		// POST http://localhost:7080/jaxrs/api/user/{userId}/model
		//
		
		UserModel modelUpdate = new UserModel(userModel)
			.withTitle("Updated" + title);
		
		url = jaxrsUrl("/api/user/" + userId + "/model");
		res = withClient(url, target -> target.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)
				.post(Entity.json(modelUpdate)));
	
		assertStatus(res, Status.OK);

		// Get Model
		
		// GET http://localhost:7080/jaxrs/api/user/{userId}/model/{modelId}
		//
		
		url = jaxrsUrl("/api/user/" + userId + "/model/" + modelId);
		res = withClient(url, target -> target.request()
				.header("Authorization", "Bearer " + accessToken)
				.get());
	
		assertStatus(res, Status.OK);
		
		userModel = res.readEntity(UserModel.class);
		Assert.assertTrue(userModel.title.startsWith("Updated"));
		
		// Delete Model
		
		// DELETE http://localhost:7080/jaxrs/api/user/{userId}/model/{modelId}
		//
		
		url = jaxrsUrl("/api/user/" + userId + "/model/" + modelId);
		res = withClient(url, target -> target.request()
				.header("Authorization", "Bearer " + accessToken)
				.delete());
	
		assertStatus(res, Status.NO_CONTENT);

		// Delete User
		
		deleteUSer(accessToken, userId);
	}

	private UserTokens registerUser() throws IOException, JsonParseException, JsonMappingException {
		
		URL resUrl = getClass().getResource("/json/user-register.json");
		
		ObjectMapper mapper = new ObjectMapper();
		UserRegister user = mapper.readValue(resUrl, UserRegister.class);
		Assert.assertEquals("myuser@example.com", user.getEmail());
		
		// Register User
		
		// PUT http://localhost:7080/tryit/api/users
		// 
		// {
		//	  "firstName": "My",
		//	  "lastName":  "User", 
		//	  "email":	   "myuser@example.com",
		//	  "username":  "myuser", 
		//	  "password":  "mypass"
		// }
		
		Response res = withClient(jaxrsUrl("/api/users"), 
				target -> target.request().put(Entity.json(user)));
		
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
		
		res = withClient(jaxrsUrl("/api/users/login"), 
				target -> target.request().post(Entity.form(data)));
		
		assertStatus(res, Status.OK);
		
		UserTokens tokens = res.readEntity(UserTokens.class);
		return tokens;
	}

	private void deleteUSer(String accessToken, String userId) {
		
		// Delete
		
		// DELETE http://localhost:7080/tryit/api/user
		// Authorization: "Bearer eyJhbGciOi..."
		
		Response res = withClient(jaxrsUrl("/api/user/" + userId), 
				target -> target.request()
					.header("Authorization", "Bearer " + accessToken)
					.delete());
		
		assertStatus(res, Status.NO_CONTENT);
	}
}
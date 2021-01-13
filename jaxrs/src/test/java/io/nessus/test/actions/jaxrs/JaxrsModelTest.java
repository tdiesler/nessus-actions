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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
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

import io.nessus.actions.core.model.RouteModel;
import io.nessus.actions.core.types.MavenBuildHandle;
import io.nessus.actions.core.types.MavenBuildHandle.BuildStatus;
import io.nessus.actions.core.utils.ApiUtils;
import io.nessus.actions.jaxrs.type.Model;
import io.nessus.actions.jaxrs.type.Model.ModelState;
import io.nessus.actions.jaxrs.type.Model.TargetRuntime;
import io.nessus.actions.jaxrs.type.ModelAdd;
import io.nessus.actions.jaxrs.type.ModelList;
import io.nessus.actions.jaxrs.type.UserRegister;
import io.nessus.actions.jaxrs.type.UserTokens;
import io.nessus.common.utils.StreamUtils;

public class JaxrsModelTest extends AbstractJaxrsTest {

	@Test
	public void testModelLifecycle() throws Exception {

		// User Register
		
		UserTokens tokens = registerUser();
		
		String accessToken = tokens.accessToken;
		String userId = tokens.userId;
		
		InputStream input = getClass().getResourceAsStream("/model/crypto-ticker.yaml");
		RouteModel routeModel = RouteModel.read(input);

		String content = routeModel.toString();
		ModelAdd modelAdd = new ModelAdd(userId, content);
		
		// Create Model
		
		// PUT http://localhost:8200/jaxrs/api/user/{userId}/models
		// 
		// {
		//	  "userId": "myuser",
		//	  "content": "some model content", 
		// }

		URI uri = jaxrsUri("/api/user/" + userId + "/models");
		Response res = withClient(uri, target -> target
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + accessToken)
					.put(Entity.json(modelAdd)));
		
		assertStatus(res, Status.CREATED);
		
		Model model = res.readEntity(Model.class);
		String modelId = model.getModelId();
		
		Assert.assertEquals(userId, model.userId);
		Assert.assertEquals(content, model.content);
		Assert.assertNotNull(modelId);
		
		// Get Models
		
		// GET http://localhost:8200/jaxrs/api/user/{userId}/models
		//
		
		res = withClient(uri, target -> target.request()
				.header("Authorization", "Bearer " + accessToken)
				.get());
	
		assertStatus(res, Status.OK);
		
		ModelList models = res.readEntity(ModelList.class);
		Assert.assertEquals(userId, models.getUserId());
		Assert.assertNotNull(models.getModels().stream().filter(m -> m.getModelId().equals(modelId)).findAny().orElse(null));
		
		// Get Model
		
		// GET http://localhost:8200/jaxrs/api/user/{userId}/model/{modelId}
		//
		
		uri = jaxrsUri("/api/user/" + userId + "/model/" + modelId);
		res = withClient(uri, target -> target.request()
				.header("Authorization", "Bearer " + accessToken)
				.get());
	
		assertStatus(res, Status.OK);
		
		model = res.readEntity(Model.class);
		Assert.assertEquals(userId, model.userId);
		Assert.assertEquals(content, model.content);
		Assert.assertNotNull(modelId);

		// Update Model
		
		// POST http://localhost:8200/jaxrs/api/user/{userId}/model/{modelId}
		//
		
		routeModel = model.getRouteModel();
		routeModel = routeModel.withTitle("Updated " + model.getTitle());
		
		Model modelUpdate = new Model(model)
			.withContent(routeModel.toString());
		
		uri = jaxrsUri("/api/user/" + userId + "/model/" + modelId);
		res = withClient(uri, target -> target.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)
				.post(Entity.json(modelUpdate)));
	
		assertStatus(res, Status.OK);

		// Get Model
		
		// GET http://localhost:8200/jaxrs/api/user/{userId}/model/{modelId}
		//
		
		uri = jaxrsUri("/api/user/" + userId + "/model/" + modelId);
		res = withClient(uri, target -> target.request()
				.header("Authorization", "Bearer " + accessToken)
				.get());
	
		assertStatus(res, Status.OK);
		
		model = res.readEntity(Model.class);
		Assert.assertTrue(model.getTitle().startsWith("Updated"));
		
		// Build Model
		
		// GET http://localhost:8200/jaxrs/api/user/{userId}/model/{modelId}/{runtime}/build
		//

		uri = jaxrsUri("/api/user/" + userId + "/model/" + modelId + "/standalone/build");
		res = withClient(uri, target -> target.request()
				.header("Authorization", "Bearer " + accessToken)
				.get());
		
		assertStatus(res, Status.OK);

		MavenBuildHandle handle = res.readEntity(MavenBuildHandle.class);
		BuildStatus buildStatus = handle.getBuildStatus();
		
		Assert.assertEquals(BuildStatus.Scheduled, buildStatus);
		
		// Get Build Status
		
		// GET http://localhost:8200/jaxrs/api/user/{userId}/model/{modelId}/{runtime}/status
		//
		
		uri = jaxrsUri("/api/user/" + userId + "/model/" + modelId + "/standalone/status");
		res = withClient(uri, target -> target.request()
				.header("Authorization", "Bearer " + accessToken)
				.get());
		
		assertStatus(res, Status.OK);
		
		handle = res.readEntity(MavenBuildHandle.class);
		buildStatus = handle.getBuildStatus();
		Assert.assertEquals(BuildStatus.Scheduled, buildStatus);
		
		while (buildStatus == BuildStatus.Scheduled || buildStatus == BuildStatus.Running) {
			
			sleepSafe(2500);
			
			res = withClient(uri, target -> target.request()
					.header("Authorization", "Bearer " + accessToken)
					.get());
			
			ApiUtils.assertStatus(res, Status.OK);
			
			handle = res.readEntity(MavenBuildHandle.class);
			buildStatus = handle.getBuildStatus();
		}

		Assert.assertEquals(BuildStatus.Success, buildStatus);
		assertStatus(res, Status.OK);

		// Get Model again to verify associated state
		
		// GET http://localhost:8200/jaxrs/api/user/{userId}/model/{modelId}
		//
		
		uri = jaxrsUri("/api/user/" + userId + "/model/" + modelId);
		res = withClient(uri, target -> target.request()
				.header("Authorization", "Bearer " + accessToken)
				.get());
	
		assertStatus(res, Status.OK);
		
		model = res.readEntity(Model.class);
		ModelState modelState = model.getModelState(TargetRuntime.standalone);
		Assert.assertEquals(BuildStatus.Success, modelState.getBuildStatus());
		
		// Download the Target File
		
		// GET http://localhost:8200/jaxrs/api/user/{userId}/model/{modelId}/{runtime}/download
		//
		
		uri = jaxrsUri("/api/user/" + userId + "/model/" + modelId + "/standalone/download");
		res = withClient(uri, target -> target.request()
				.header("Authorization", "Bearer " + accessToken)
				.get());
		
		assertStatus(res, Status.OK);

		String contentDisposition = res.getHeaderString("Content-Disposition");
		logInfo("Content-Disposition: {}", contentDisposition);
		Assert.assertTrue(contentDisposition.startsWith("attachment;filename="));

		int fnameIdx = contentDisposition.indexOf('=') + 1;
		String fileName = contentDisposition.substring(fnameIdx);
		File targetFile = new File("target/" + fileName);
		
		try (FileOutputStream fos = new FileOutputStream(targetFile)) {
			InputStream ins = res.readEntity(InputStream.class);
			StreamUtils.copyStream(ins, fos);
		}
		
		logInfo("Downloaded: {}", targetFile);
		Assert.assertTrue(targetFile.isFile());
		Assert.assertTrue(targetFile.getName().endsWith("crypto-ticker-1.0.0-runner.jar"));

		// Download the Project sources
		
		// GET http://localhost:8200/jaxrs/api/user/{userId}/model/{modelId}/{runtime}/sources
		//
		
		uri = jaxrsUri("/api/user/" + userId + "/model/" + modelId + "/standalone/sources");
		res = withClient(uri, target -> target.request()
				.header("Authorization", "Bearer " + accessToken)
				.get());
		
		assertStatus(res, Status.OK);

		contentDisposition = res.getHeaderString("Content-Disposition");
		logInfo("Content-Disposition: {}", contentDisposition);
		Assert.assertTrue(contentDisposition.startsWith("attachment;filename="));

		fnameIdx = contentDisposition.indexOf('=') + 1;
		fileName = contentDisposition.substring(fnameIdx);
		File srcTargetFile = new File("target/" + fileName);
		
		try (FileOutputStream fos = new FileOutputStream(srcTargetFile)) {
			InputStream ins = res.readEntity(InputStream.class);
			StreamUtils.copyStream(ins, fos);
		}
		
		logInfo("Downloaded: {}", srcTargetFile);
		Assert.assertTrue(srcTargetFile.isFile());
		Assert.assertTrue(srcTargetFile.getName().endsWith("standalone-maven-project.tgz"));

		// Delete Model
		
		// DELETE http://localhost:8200/jaxrs/api/user/{userId}/model/{modelId}
		//
		
		uri = jaxrsUri("/api/user/" + userId + "/model/" + modelId);
		res = withClient(uri, target -> target.request()
				.header("Authorization", "Bearer " + accessToken)
				.delete());
	
		assertStatus(res, Status.NO_CONTENT);

		// Delete User
		
		userDelete(accessToken, userId);
	}

	private UserTokens registerUser() throws IOException, JsonParseException, JsonMappingException {
		
		URL resUrl = getClass().getResource("/json/user-register.json");
		
		ObjectMapper mapper = new ObjectMapper();
		UserRegister user = mapper.readValue(resUrl, UserRegister.class);
		Assert.assertEquals("myuser@example.com", user.getEmail());
		
		// User Register
		
		// PUT http://localhost:8200/tryit/api/users
		// 
		// {
		//	  "firstName": "My",
		//	  "lastName":  "User", 
		//	  "email":	   "myuser@example.com",
		//	  "username":  "myuser", 
		//	  "password":  "mypass"
		// }
		
		Response res = withClient(jaxrsUri("/api/users"), 
				target -> target.request().put(Entity.json(user)));
		
		assertStatus(res, Status.CREATED, Status.CONFLICT);
		
		// User Login
		
		// POST http://localhost:8200/tryit/api/user/token
		// Content-Type: application/x-www-form-urlencoded
		//
		// username: myuser 
		// password: mypass
		
		MultivaluedMap<String, String> data = new MultivaluedHashMap<>();
		data.add("username", user.getUsername());
		data.add("password", user.getPassword());
		
		res = withClient(jaxrsUri("/api/users/login"), 
				target -> target.request().post(Entity.form(data)));
		
		assertStatus(res, Status.OK);
		
		UserTokens tokens = res.readEntity(UserTokens.class);
		return tokens;
	}

	private void userDelete(String accessToken, String userId) {
		
		// Delete
		
		// DELETE http://localhost:8200/tryit/api/user
		// Authorization: "Bearer eyJhbGciOi..."
		
		Response res = withClient(jaxrsUri("/api/user/" + userId), 
				target -> target.request()
					.header("Authorization", "Bearer " + accessToken)
					.delete());
		
		assertStatus(res, Status.NO_CONTENT);
	}
}
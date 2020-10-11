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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.nessus.actions.jaxrs.service.KeycloakService;
import io.nessus.actions.jaxrs.type.KeycloakTokens;
import io.nessus.actions.jaxrs.type.User;
import io.nessus.actions.jaxrs.type.UserInfo;

public class JaxrsStressTest extends AbstractJaxrsTest {

	@Test
	public void testUserLifecycle() throws Exception {

		int loop = 10;
		int threads = 10;
		int cycles = threads * loop;
		
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		
		URL resUrl = getClass().getResource("/json/user-register.json");
		ObjectMapper mapper = new ObjectMapper();
		User bu = mapper.readValue(resUrl, User.class);
		
		CountDownLatch latch = new CountDownLatch(cycles);
		List<User> completed = new ArrayList<User>();
		
		long before = System.currentTimeMillis();
		
		for (int i = 0; i < threads; i++) {
			
			int threadIdx = i;
			
			executor.submit(() -> {
				
				int startIdx = loop * threadIdx;

				for (int j = startIdx; j < startIdx + loop; j++) {
					
					User user = new User(
							j + bu.getFirstName(),
							j + bu.getLastName(),
							j + bu.getEmail(),
							String.format("%03d%s", j, bu.getUsername()),
							j + bu.getPassword());
					
					workcycle(j, user);
					
					completed.add(user);
					latch.countDown();
					
					sleepSafe((int) (100 * Math.random()));
				}
			});
		}
		
		latch.await(5, TimeUnit.MINUTES);
		long now = System.currentTimeMillis();
		
		logInfo("{} cycles in {}ms", cycles, now - before);
		Assert.assertEquals(cycles, completed.size());
	}

	public void workcycle(int id, User user) {

		logInfo("{}: {}", id, user);
		
		// Register
		
		Response res = withClient(jaxrsUrl("/api/users"), 
				target -> target.request().post(Entity.json(user)));
		
		assertStatus(res, Status.CREATED, Status.CONFLICT);
		
		// Login
		
		MultivaluedMap<String, String> reqmap = new MultivaluedHashMap<>();
		reqmap.add("username", user.getUsername());
		reqmap.add("password", user.getPassword());
		
		res = withClient(jaxrsUrl("/api/user/token"), 
				target -> target.request().post(Entity.form(reqmap)));
		
		assertStatus(res, Status.OK);
		KeycloakTokens tokens = res.readEntity(KeycloakTokens.class);

		// Status
		
		KeycloakService kcsrv = getService(KeycloakService.class);
		String accessToken = kcsrv.refreshAccessToken(tokens.refreshToken);
		Assert.assertNotNull("Null access token", accessToken);
		
		res = withClient(jaxrsUrl("/api/user/status"), 
				target -> target.request()
					.header("Authorization", "Bearer " + accessToken)
					.get());
		
		assertStatus(res, Status.OK);
		
		UserInfo userInfo = res.readEntity(UserInfo.class);
		Assert.assertEquals(user.getEmail(), userInfo.getEmail());
		
		// Delete
		
		res = withClient(jaxrsUrl("/api/user"), 
				target -> target.request()
					.header("Authorization", "Bearer " + accessToken)
					.delete());
		
		assertStatus(res, Status.NO_CONTENT);
	}
}
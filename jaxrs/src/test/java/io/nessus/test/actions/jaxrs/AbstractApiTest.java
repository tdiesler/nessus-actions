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

import static io.nessus.actions.jaxrs.utils.JaxrsUtils.hasStatus;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;

import io.nessus.actions.jaxrs.ApiApplication;
import io.nessus.actions.jaxrs.JaxrsConfig;
import io.nessus.actions.jaxrs.JaxrsServer;
import io.nessus.actions.jaxrs.main.JaxrsMain;
import io.nessus.actions.jaxrs.service.JaxrsService;
import io.nessus.common.Config;
import io.nessus.common.testing.AbstractTest;

abstract class AbstractApiTest extends AbstractTest {

	private static UndertowJaxrsServer server;

	@Before
	public void before() throws Exception {
		if (server == null) {
			server = createJaxrsServer();
			if (server != null) {
				server.start();
			}
		}
	}

	@AfterClass
	public static void afterClass() throws Exception {
		if (server != null) {
			server.stop();
			server = null;
		}
	}

	@After
	public void after() throws Exception {
		// do nothing
	}

	@Override
	protected Config createConfig() {
		ApiApplication api = ApiApplication.getInstance();
		JaxrsConfig config = api.getConfig();
		return config;
	}

	@Override
	public JaxrsConfig getConfig() {
		return (JaxrsConfig) super.getConfig();
	}

	protected JaxrsServer createJaxrsServer() throws IOException, Exception {
		JaxrsMain main = new JaxrsMain(getConfig());
		return main.createJaxrsServer();
	}

	protected Response withClient(String uri, Function<WebTarget, Response> invoker) {
		JaxrsService jaxrs = getService(JaxrsService.class);
		return jaxrs.withClient(uri, invoker);
	}

	protected void assertStatus(Response res, Status... exp) {
		if (!hasStatus(res, exp)) {
			int status = res.getStatus();
			String reason = res.getStatusInfo().getReasonPhrase();
			Assert.fail(String.format("[%d %s] not in %s", status, reason, Arrays.asList(exp)));
		}
	}
}
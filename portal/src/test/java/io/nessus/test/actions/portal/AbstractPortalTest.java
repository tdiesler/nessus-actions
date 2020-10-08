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

import static io.nessus.actions.portal.api.ApiUtils.hasStatus;

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

import io.nessus.actions.portal.PortalConfig;
import io.nessus.actions.portal.PortalMain;
import io.nessus.actions.portal.api.PortalApi;
import io.nessus.actions.portal.service.ApiService;
import io.nessus.common.Config;
import io.nessus.common.testing.AbstractTest;

abstract class AbstractPortalTest extends AbstractTest {

	private static UndertowJaxrsServer server;

	@AfterClass
	public static void afterClass() throws Exception {
		if (server != null) {
			server.stop();
			server = null;
		}
	}

	@Before
	public void before() throws Exception {
		if (server == null) {
			PortalMain main = new PortalMain(getConfig());
			server = main.startPortalServer();
		}
	}

	@After
	public void after() throws Exception {
		// do nothing
	}

	@Override
	protected Config createConfig() {
		return PortalApi.getInstance().getConfig();
	}

	@Override
	public PortalConfig getConfig() {
		return (PortalConfig) super.getConfig();
	}

	protected Response withClient(String uri, Function<WebTarget, Response> function) {
		ApiService apisrv = getService(ApiService.class);
		return apisrv.withClient(uri, function);
	}

	protected void assertStatus(Response res, Status... exp) {
		if (!hasStatus(res, exp)) {
			int status = res.getStatus();
			String reason = res.getStatusInfo().getReasonPhrase();
			Assert.fail(String.format("[%d %s] not in %s", status, reason, Arrays.asList(exp)));
		}
	}
}
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

import static io.nessus.actions.portal.ApiUtils.hasStatus;

import java.net.URL;
import java.util.Arrays;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

import io.nessus.actions.portal.PortalConfig;
import io.nessus.actions.portal.resources.ApiService;
import io.nessus.actions.portal.resources.PortalApi;
import io.nessus.common.Config;
import io.nessus.common.testing.AbstractTest;

abstract class AbstractPortalTest extends AbstractTest {

	private static UndertowJaxrsServer server;

	@BeforeClass
	public static void beforeClass() throws Exception {
		
		PortalConfig config = PortalConfig.createConfig();
		URL url = new URL(config.getPortalUrl());
		
		server = new UndertowJaxrsServer()
				.setHostname(url.getHost())
				.setPort(url.getPort())
				.start();
		
		server.deployOldStyle(PortalApi.class);
	}

	@AfterClass
	public static void afterClass() throws Exception {
		server.stop();
	}

	@Before
	public void before() throws Exception {
	}

	@After
	public void after() throws Exception {
	}

	@Override
	protected Config createConfig() {
		PortalConfig config = PortalConfig.createConfig();
		config.addService(new ApiService(config));
		return config;
	}

	@Override
	public PortalConfig getConfig() {
		return (PortalConfig) super.getConfig();
	}

	protected void assertStatus(Response res, Status... exp) {
		if (!hasStatus(res, exp)) {
			int status = res.getStatus();
			String reason = res.getStatusInfo().getReasonPhrase();
			Assert.fail(String.format("[%d %s] not in expected %s", status, reason, Arrays.asList(exp)));
		}
	}
}
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
package io.nessus.actions.test.camel;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import io.nessus.actions.portal.resources.PortalApi;
import io.nessus.actions.testing.AbstractTest;

abstract class AbstractApiTest extends AbstractTest {

	private static UndertowJaxrsServer server;

	protected Client client;
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		server = new UndertowJaxrsServer().start();
		server.deployOldStyle(PortalApi.class);
	}

	@AfterClass
	public static void afterClass() throws Exception {
		server.stop();
	}

	@Before
	public void before() throws Exception {
		client = ClientBuilder.newClient();
	}

	@After
	public void after() throws Exception {
		if (client != null)
			client.close();
	}
}
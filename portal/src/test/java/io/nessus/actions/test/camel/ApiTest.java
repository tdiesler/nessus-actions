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

import static io.nessus.actions.portal.resources.AbstractResource.asText;

import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import io.nessus.actions.portal.resources.ApiRoot;
import io.nessus.actions.testing.AbstractTest;
import io.nessus.actions.testing.HttpRequest;
import io.nessus.actions.testing.HttpRequest.HttpResponse;

public class ApiTest extends AbstractTest {

	private static UndertowJaxrsServer server;

	@BeforeClass
	public static void beforeClass() throws Exception {
		server = new UndertowJaxrsServer().start();
		server.deployOldStyle(ApiRoot.class);
	}

	@AfterClass
	public static void afterClass() throws Exception {
		server.stop();
	}

	@Test
	public void testStatus() throws Exception {
		
		HttpResponse res = HttpRequest.get(generateURL("/api/status")).getResponse();
		Assert.assertEquals(200, res.getStatusCode());
		Assert.assertEquals("online", asText(res.getBody(), "status"));
	}
}
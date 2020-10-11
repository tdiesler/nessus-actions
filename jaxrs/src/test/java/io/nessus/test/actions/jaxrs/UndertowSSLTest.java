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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nessus.actions.jaxrs.utils.SSLContextBuilder;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.util.HeaderValues;

public class UndertowSSLTest {

	static final Logger LOG = LoggerFactory.getLogger(UndertowSSLTest.class);
	
	/*
	 * https://access.redhat.com/solutions/973783
	 * 
	 * -Djavax.net.debug=ssl,handshake
	 * -Djavax.net.debug=all
	 */
	@Test
	public void testHttps() throws Exception {

		Path srcdir = Paths.get("src/test/resources/tls-local");
		
		String alias = "self-signed-local";
		Path certPath = srcdir.resolve("tls.crt");
		Path privKeyPath = srcdir.resolve("tls.key");
		
		SSLContext sslContext = new SSLContextBuilder()
				.keystorePath(Paths.get("target/keystore-local.p12"))
				.keystoreType("pkcs12")
				.addPrivateKey(alias, privKeyPath)
				.addCertificate(alias, certPath)
				.build();
		
		SSLContext.setDefault(sslContext);
		
		String host = "127.0.0.1";
		int port = 8443;
		
		Undertow server = Undertow.builder()
			.addHttpsListener(port, host, SSLContext.getDefault())
			.setHandler(handler)
			.build();
		
		server.start();

		try {
			
			String url = String.format("https://%s:%d", host, port);
			Response res = withClient(url, target -> target.request().get());
			Assert.assertEquals("Hello World", res.readEntity(String.class));
			
		} finally {
			
			server.stop();
		}
	}

	@Test
	public void testHttp() throws Exception {

		String host = "127.0.0.1";
		int port = 8080;
		
		Undertow server = Undertow.builder()
			.addHttpListener(port, host)
			.setHandler(handler)
			.build();
		
		server.start();
		
		try {
			
			String url = String.format("http://%s:%d", host, port);
			Response res = withClient(url, target -> target.request().get());
			Assert.assertEquals("Hello World", res.readEntity(String.class));
			
		} finally {
			
			server.stop();
		}
	}

	private HttpHandler handler = exchange -> {
		HeaderValues userAgent = exchange.getRequestHeaders().get("User-Agent");
		exchange.getResponseSender().send("Hello World");
		LOG.info("User-Agent: {}", userAgent);
	};

	private Response withClient(String uri, Function<WebTarget, Response> invoker) throws Exception {
		
		long before = System.currentTimeMillis();
		
		Client client = ClientBuilder.newBuilder()
				.sslContext(SSLContext.getDefault())
				.build();
		
		try {
			
			WebTarget target = client.target(uri);
			Response res = invoker.apply(target);
			
			long now = System.currentTimeMillis();
			int status = res.getStatus();
			String reason = res.getStatusInfo().getReasonPhrase();
			LOG.info("{} => [{} {}] in {}ms", uri, status, reason, now - before);
			
			return res;
			
		} finally {
			
			client.close();
		}
	}
}
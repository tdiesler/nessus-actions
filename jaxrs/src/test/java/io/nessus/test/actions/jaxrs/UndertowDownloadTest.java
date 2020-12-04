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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.nessus.actions.jaxrs.type.UserRegister;
import io.nessus.common.AssertState;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;

public class UndertowDownloadTest {

	static final Logger LOG = LoggerFactory.getLogger(UndertowDownloadTest.class);
	
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
			
			Client client = ClientBuilder.newBuilder().build();
			try {
				
				String uri = String.format("http://%s:%d", host, port);
				
				int numThreads = 8;
				CountDownLatch latch = new CountDownLatch(numThreads);
				
				ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
				for (int i = 0; i < numThreads; i++) {
					
					executorService.submit(() -> {
						
						long before = System.currentTimeMillis();
						
						try {
							Response res = client.target(uri)
									.request(MediaType.APPLICATION_OCTET_STREAM)
									.get();
							
							int status = res.getStatus();
							Assert.assertEquals(200, status);
							
							String contentDisposition = res.getHeaderString("Content-Disposition");
							Assert.assertTrue(contentDisposition.startsWith("attachment;filename="));
							
							String contentType = res.getHeaderString("Content-Type");
							Assert.assertEquals(MediaType.APPLICATION_OCTET_STREAM, contentType);
							
							InputStream inputStream = res.readEntity(InputStream.class);
							
							ObjectMapper mapper = new ObjectMapper();
							UserRegister register = mapper.readValue(inputStream, UserRegister.class);
							Assert.assertEquals("myuser@example.com", register.getEmail());
							
							long now = System.currentTimeMillis();
							Thread currThread = Thread.currentThread();
							String reason = res.getStatusInfo().getReasonPhrase();
							LOG.info("{} => [{} {}] in {}ms", currThread, status, reason, now - before);
							
						} catch (Exception ex) {
							
							LOG.error("Execution error", ex);
							
						} finally {
							
							latch.countDown();
						}
					});
				}
				
				if (!latch.await(10, TimeUnit.SECONDS))
					throw new TimeoutException();
				
			} finally {
				
				client.close();
			}
			
		} finally {
			
			server.stop();
		}
	}

	private HttpHandler handler = exchange -> {
		
        AssertState.isTrue(exchange.isInIoThread());

        exchange.startBlocking();
        
        exchange.dispatch(dispex -> {
        	
    		File file = new File("src/test/resources/json/user-register.json");
    		AssertState.isTrue(file.exists(), "File does not exist: " + file);
    		
    		HeaderMap responseHeaders = exchange.getResponseHeaders();
    		responseHeaders.put(new HttpString("Content-Disposition"), "attachment;filename=" + file.getName());
    		responseHeaders.put(new HttpString("Content-Type"), MediaType.APPLICATION_OCTET_STREAM);
    		
    		try (InputStream inputStream = new FileInputStream(file)) {
    			try (OutputStream outputStream = exchange.getOutputStream()) {
    				byte[] buffer = new byte[64];
    				int len = inputStream.read(buffer);
    				while (len > 0) {
    					LOG.info("{} writing {} bytes ...", Thread.currentThread(), len);
    					outputStream.write(buffer, 0, len);
    					len = inputStream.read(buffer);
    				}
    			}
    		}
        });
	};
}
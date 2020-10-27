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

import java.net.ConnectException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.net.ssl.SSLContext;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import io.nessus.actions.jaxrs.service.KeycloakService;
import io.nessus.common.rest.JaxrsServer;
import io.nessus.common.rest.SSLContextBuilder;

public class KeycloakTest extends AbstractJaxrsTest {

	@Override
	protected JaxrsServer createJaxrsServer() {
		return null;
	}

	/*
	 * https://access.redhat.com/solutions/973783
	 * 
	 * -Djavax.net.debug=ssl,handshake
	 * -Djavax.net.debug=all
	 */
	@Test
	public void testHttps() throws Exception {

		Path srcdir = Paths.get("src/test/resources/tls-public");
		
		String alias = "keycloak-self-signed";
		Path certPath = srcdir.resolve("tls.crt");
		Path keyPath = srcdir.resolve("tls.key");
		
		SSLContext sslContext = new SSLContextBuilder()
				.keystorePath(Paths.get("target/keycloak-keystore.jks"))
				.addCertificate(alias, certPath)
				.addPrivateKey(alias, keyPath)
				.build();
		
		SSLContext.setDefault(sslContext);
		
		getConfig().setUseTLS(true);
		
		try {
			String accessToken = getService(KeycloakService.class).getMasterAccessToken();
			Assert.assertNotNull(accessToken);
		} catch (Exception ex) {
			Exception cause = getRootCause(ex);
			Assume.assumeFalse(cause instanceof ConnectException);
			throw ex;
		}
	}


	@Test
	public void testMasterAccessToken() throws Exception {

		KeycloakService kcsrv = getService(KeycloakService.class);

		String masterAccessToken = kcsrv.getMasterAccessToken();
		Assert.assertNotNull(masterAccessToken);
		
		String refreshToken = getConfig().getMasterRefreshToken();
		Assert.assertNotNull(refreshToken);
	}
	
	private Exception getRootCause(Exception ex) {
		Throwable cause = ex.getCause();
		if (cause == null) return ex;
		while (cause != ex) {
			Throwable aux = cause.getCause();
			if (aux == null) break;
			cause = aux;
		}
		return (Exception) cause;
	}
}


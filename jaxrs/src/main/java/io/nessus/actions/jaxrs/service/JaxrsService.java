package io.nessus.actions.jaxrs.service;

import java.security.NoSuchAlgorithmException;
import java.util.function.Function;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import io.nessus.actions.jaxrs.main.JaxrsConfig;
import io.nessus.common.CheckedExceptionWrapper;

public class JaxrsService extends AbstractService<JaxrsConfig> {

	public JaxrsService(JaxrsConfig config) {
		super(config);
	}

	public Response withClient(String uri, Function<WebTarget, Response> invoker) {
		
		ClientBuilder builder = ClientBuilder.newBuilder();
		
		if (uri.startsWith("https://")) {
			try {
				builder.sslContext(SSLContext.getDefault());
			} catch (NoSuchAlgorithmException ex) {
				throw CheckedExceptionWrapper.create(ex);
			}
		}
		
		Client client = builder.build();
		try {
			
			long before = System.currentTimeMillis();
			
			WebTarget target = client.target(uri);
			Response res = invoker.apply(target);
			
			long now = System.currentTimeMillis();
			int status = res.getStatus();
			String reason = res.getStatusInfo().getReasonPhrase();
			logInfo("{} => [{} {}] in {}ms", uri, status, reason, now - before);
			
			res.bufferEntity();
			
			return res;
			
		} finally {
			
			client.close();
		}
	}
}
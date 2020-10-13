package io.nessus.actions.jaxrs;

import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import io.nessus.actions.jaxrs.main.JaxrsConfig;
import io.nessus.actions.jaxrs.service.JaxrsService;
import io.nessus.actions.jaxrs.service.KeycloakService;
import io.nessus.actions.jaxrs.utils.JaxrsUtils;
import io.nessus.actions.jaxrs.utils.KeycloakUtils;
import io.nessus.common.ConfigSupport;

public abstract class AbstractResource<T extends JaxrsConfig> extends ConfigSupport<T> {
	
	@Context
	protected HttpServletRequest httpRequest;
	
	protected AbstractResource(T config) {
		super(config);
	}

	protected String getAccessToken() {
		
		String authHeader = httpRequest.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer"))
			return null;
		
		String accessToken = authHeader.split(" ")[1];
		return accessToken;
	}

	protected Response withClient(String uri, Function<WebTarget, Response> invoker) {
		JaxrsService jaxrs = getService(JaxrsService.class);
		return jaxrs.withClient(uri, invoker);
	}

	protected String jaxrsUrl(String path) {
		boolean useTLS = config.isUseTLS();
		return JaxrsUtils.jaxrsUrl(path, useTLS);
	}

	protected String keycloakUrl(String path) {
		boolean useTLS = config.isUseTLS();
		return KeycloakUtils.keycloakUrl(path, useTLS);
	}

	protected KeycloakService getKeycloakService() {
		return config.getService(KeycloakService.class);
	}
	
	protected JaxrsService getJaxrsService() {
		return config.getService(JaxrsService.class);
	}
}
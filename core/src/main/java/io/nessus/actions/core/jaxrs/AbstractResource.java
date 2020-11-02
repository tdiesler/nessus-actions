package io.nessus.actions.core.jaxrs;

import java.net.URI;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import io.nessus.actions.core.NessusConfig;
import io.nessus.actions.core.NessusConfigHolder;
import io.nessus.actions.core.service.KeycloakService;
import io.nessus.common.ConfigSupport;

public abstract class AbstractResource extends ConfigSupport<NessusConfig> {
	
	@Context
	protected HttpServletRequest httpRequest;
	
	protected AbstractResource() {
		super(NessusConfigHolder.getConfig());
	}

	protected String getAccessToken() {
		
		String authHeader = httpRequest.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer"))
			return null;
		
		String accessToken = authHeader.split(" ")[1];
		return accessToken;
	}

	protected Response withClient(URI uri, Function<WebTarget, Response> invoker) {
		KeycloakService keycloak = getService(KeycloakService.class);
		return keycloak.withClient(uri, invoker);
	}

	protected KeycloakService getKeycloakService() {
		return config.getService(KeycloakService.class);
	}
}
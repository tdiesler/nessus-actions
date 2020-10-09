package io.nessus.actions.jaxrs;

import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import io.nessus.actions.jaxrs.service.ApiService;
import io.nessus.common.ConfigSupport;

public abstract class AbstractResource extends ConfigSupport<ApiConfig> {
	
	@Context
	protected HttpServletRequest httpRequest;
	
	protected final ApiRoot api;
	
	protected AbstractResource() {
		super(ApiRoot.getInstance().getConfig());
		this.api = ApiRoot.getInstance();
	}

	protected String getAccessToken() {
		
		String authHeader = httpRequest.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer"))
			return null;
		
		String accessToken = authHeader.split(" ")[1];
		return accessToken;
	}

	protected Response withClient(String uri, Function<WebTarget, Response> function) {
		ApiService apisrv = api.getApiService();
		return apisrv.withClient(uri, function);
	}
}
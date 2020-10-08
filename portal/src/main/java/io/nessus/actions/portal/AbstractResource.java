package io.nessus.actions.portal;

import java.util.function.Function;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import io.nessus.actions.portal.api.PortalApi;
import io.nessus.actions.portal.service.ApiService;
import io.nessus.common.ConfigSupport;

public abstract class AbstractResource extends ConfigSupport<PortalConfig> {
	
	protected final PortalApi api;
	
	protected AbstractResource() {
		super(PortalApi.getInstance().getConfig());
		this.api = PortalApi.getInstance();
	}

	public Response withClient(String uri, Function<WebTarget, Response> function) {
		ApiService apisrv = api.getApiService();
		return apisrv.withClient(uri, function);
	}
}
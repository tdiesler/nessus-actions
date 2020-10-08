package io.nessus.actions.portal.api;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import io.nessus.actions.portal.service.ApiService;

@Path("/user/token")
public class ApiUserToken extends AbstractApiResource {
	
	@POST
	public Response post() {
		
		String username = httpRequest.getParameter("username");
		String password = httpRequest.getParameter("password");
		
		LOG.info("Login: {}", username);
		
		ApiService apisrv = api.getApiService();
		Response res = apisrv.getUserTokens(username, password);
		
		return res;
	}
}
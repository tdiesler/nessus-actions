package io.nessus.actions.jaxrs;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import io.nessus.actions.jaxrs.service.ApiService;

@Path("/user/token")
public class ApiUserToken extends AbstractResource {
	
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
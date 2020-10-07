package io.nessus.actions.portal.api;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/login")
public class ApiUserLogin extends AbstractApiResource {
	
	@POST
	public Response post() {
		
		String username = httpRequest.getParameter("username");
		String password = httpRequest.getParameter("password");
		
		LOG.info("Login: {}", username);
		
		ApiService apisrv = api.getApiService();
		Response res = apisrv.userLogin(username, password);
		
		return res;
	}
}
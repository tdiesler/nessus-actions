package io.nessus.actions.portal.api;

import javax.servlet.http.HttpSession;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/users/logout")
public class ApiUserLogout extends AbstractApiResource {
	
	@POST
	public Response post() {
		
		HttpSession session = httpRequest.getSession(false);
		String refreshToken = (String) session.getAttribute("refresh_token");
		
		LOG.info("Logout: {}", refreshToken);
		
		return Response.ok().build();
	}
}
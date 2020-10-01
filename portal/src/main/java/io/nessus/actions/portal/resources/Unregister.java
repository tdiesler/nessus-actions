package io.nessus.actions.portal.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/unregister")
public class Unregister extends AbstractResource {
	
	@GET
	public Response get(@QueryParam("email") String email) {
		
		Register registry = ApiRoot.getUserRegistry();
		
		Response res;
		if (registry.unregister(email)) {
			res = Response.ok(toJson(new MessageResponse("success"))).build();
		} else {
			res = Response.status(403).entity("failed").build();
		}
		return res;
	}
}
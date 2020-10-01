package io.nessus.actions.portal.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

@Path("/api/status")
public class PortalStatus extends AbstractResource {
	
	@GET
	public Response get() {
		StatusResponse entity = new StatusResponse("online");
		Response res = Response.ok(entity, MediaType.APPLICATION_JSON).build();
		return res;
	}
	
	public static class StatusResponse {

		final String status;

		@JsonCreator
		StatusResponse(@JsonProperty("status") String status) {
			this.status = status;
		}

		@JsonGetter
		public String getStatus() {
			return status;
		}
	}
}
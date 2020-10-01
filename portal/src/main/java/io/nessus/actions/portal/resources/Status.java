package io.nessus.actions.portal.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.fasterxml.jackson.annotation.JsonGetter;

@Path("/status")
public class Status extends AbstractResource {
	
	@GET
	public String get() {
		return toJson(new StatusResponse("online"));
	}
	
	static class StatusResponse {

		final String status;

		StatusResponse(String status) {
			this.status = status;
		}

		@JsonGetter
		String getStatus() {
			return status;
		}
	}
}
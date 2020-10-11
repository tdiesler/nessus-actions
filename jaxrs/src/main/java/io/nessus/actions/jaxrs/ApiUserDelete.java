package io.nessus.actions.jaxrs;

import static io.nessus.actions.jaxrs.utils.JaxrsUtils.hasStatus;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.nessus.actions.jaxrs.service.KeycloakService;
import io.nessus.actions.jaxrs.type.KeycloakUserInfo;

@Path("/user")
public class ApiUserDelete extends AbstractResource {
	
	@DELETE
	public Response delete() {
		
		// Verify user authorization
		
		String accessToken = getAccessToken();
		if (accessToken == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		KeycloakService kcsrv = api.getApiService();
		Response res = kcsrv.getUserInfo(accessToken);
		
		if (!hasStatus(res, Status.OK)) {
			return res;
		}
		
		// Get the user id 
		
		KeycloakUserInfo userInfo = res.readEntity(KeycloakUserInfo.class);
		String id = userInfo.subject;
		
		// Delete the user
		
		String realmId = config.getRealmId();
		String masterToken = kcsrv.getMasterAccessToken();
		res = withClient(keycloakUrl("/admin/realms/" + realmId + "/users/" + id), 
				target -> target.request()
				.header("Authorization", "Bearer " + masterToken)
				.delete());
		
		hasStatus(res, Status.NO_CONTENT);
		
		return res;
	}
}
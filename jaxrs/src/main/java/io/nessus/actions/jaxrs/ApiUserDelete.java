package io.nessus.actions.jaxrs;

import static io.nessus.actions.jaxrs.ApiUtils.hasStatus;
import static io.nessus.actions.jaxrs.ApiUtils.keycloakUrl;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.nessus.actions.jaxrs.service.ApiService;
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
		
		ApiService apisrv = api.getApiService();
		Response res = apisrv.getUserInfo(accessToken);
		
		if (!hasStatus(res, Status.OK)) {
			return res;
		}
		
		// Get the user id 
		
		KeycloakUserInfo userInfo = res.readEntity(KeycloakUserInfo.class);
		String id = userInfo.subject;
		
		// Delete the user
		
		String realmId = config.getRealmId();
		String masterToken = apisrv.getMasterAccessToken();
		res = withClient(keycloakUrl("/admin/realms/" + realmId + "/users/" + id), 
				target -> target.request()
				.header("Authorization", "Bearer " + masterToken)
				.delete());
		
		hasStatus(res, Status.NO_CONTENT);
		
		return res;
	}
}
package io.nessus.actions.jaxrs;

import static io.nessus.actions.jaxrs.ApiUtils.hasStatus;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.nessus.actions.jaxrs.service.ApiService;
import io.nessus.actions.jaxrs.type.KeycloakUserInfo;
import io.nessus.actions.jaxrs.type.UserInfo;

@Path("/user/status")
public class ApiUserStatus extends AbstractResource {
	
	@GET
	public Response get() {
		
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
		
		KeycloakUserInfo kcinfo = res.readEntity(KeycloakUserInfo.class);
		UserInfo userInfo = new UserInfo(kcinfo);
		
		return Response.ok(userInfo, MediaType.APPLICATION_JSON).build();
	}
}
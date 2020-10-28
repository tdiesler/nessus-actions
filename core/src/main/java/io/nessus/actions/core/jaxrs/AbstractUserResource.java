package io.nessus.actions.core.jaxrs;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.nessus.actions.core.service.KeycloakService;
import io.nessus.actions.core.types.KeycloakUserInfo;
import io.nessus.actions.core.utils.ApiUtils;

public abstract class AbstractUserResource extends AbstractResource {
	
	protected KeycloakUserInfo getKeycloakUserInfo(String userId) {
		
		// Verify user authorization
		
		String accessToken = getAccessToken();
		if (accessToken == null) {
			return null;
		}
		
		KeycloakService keycloak = getKeycloakService();
		Response res = keycloak.getKeycloakUserInfo(accessToken);
		
		if (!ApiUtils.hasStatus(res, Status.OK)) {
			return null;
		}
		
		KeycloakUserInfo kcinfo = res.readEntity(KeycloakUserInfo.class);
		if (!kcinfo.subject.equals(userId)) {
			logError("User id does not match the access token");
			return null;
		}
		
		return kcinfo;
	}
	
}
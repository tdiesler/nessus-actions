package io.nessus.actions.jaxrs;

import static io.nessus.actions.jaxrs.utils.JaxrsUtils.hasStatus;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.nessus.actions.jaxrs.service.KeycloakService;
import io.nessus.actions.jaxrs.type.KeycloakUserInfo;

public abstract class AbstractUserResource extends AbstractApiResource {
	
	protected KeycloakUserInfo getKeycloakUserInfo(String userId) {
		
		// Verify user authorization
		
		String accessToken = getAccessToken();
		if (accessToken == null) {
			return null;
		}
		
		KeycloakService keycloak = getKeycloakService();
		Response res = keycloak.getKeycloakUserInfo(accessToken);
		
		if (!hasStatus(res, Status.OK)) {
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
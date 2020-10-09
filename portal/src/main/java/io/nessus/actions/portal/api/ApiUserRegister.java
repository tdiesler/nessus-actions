package io.nessus.actions.portal.api;

import static io.nessus.actions.portal.api.ApiUtils.hasStatus;
import static io.nessus.actions.portal.api.ApiUtils.keycloakUrl;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.nessus.actions.portal.PortalConfig;
import io.nessus.actions.portal.api.type.User;
import io.nessus.actions.portal.api.type.KeycloakUserRegister;
import io.nessus.actions.portal.service.ApiService;

/**
 * User self registration requires the a master access token.
 * 
 * Here is the (unfortunate) reason why 
 * https://issues.redhat.com/browse/KEYCLOAK-15820
 * 
 * 1) Request a master access token
 * 2) Post the user create request
 * 3) Get the refresh token for the user
 * 3) Create the user session (cookie)
 *  
 */
@Path("/users")
public class ApiUserRegister extends AbstractApiResource {
	
	@POST
	public Response post(User user) {
		
		LOG.info("Register: {}", user.getEmail());
		
		ApiService apisrv = api.getApiService();
		PortalConfig config = api.getConfig();
		
		String realmId = config.getRealmId();
		String accessToken = apisrv.getMasterAccessToken();
		
		// Create the user record
		
		Response res = withClient(keycloakUrl("/admin/realms/" + realmId + "/users"),
				target -> target.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)
				.post(Entity.json(new KeycloakUserRegister(user))));
		
		hasStatus(res, Status.CREATED);
		
		return res;
	}
}
package io.nessus.actions.jaxrs;

import static io.nessus.actions.jaxrs.utils.JaxrsUtils.hasStatus;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.nessus.actions.jaxrs.service.KeycloakService;
import io.nessus.actions.jaxrs.type.KeycloakUserRegister;
import io.nessus.actions.jaxrs.type.User;
import io.nessus.actions.jaxrs.utils.KeycloakUtils;

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
public class ApiUserRegister extends AbstractResource {
	
	@POST
	public Response post(User user) {
		
		logInfo("Register: {}", user.getEmail());
		
		KeycloakService apisrv = api.getApiService();
		JaxrsConfig config = api.getConfig();
		
		String realmId = config.getRealmId();
		String accessToken = apisrv.getMasterAccessToken();
		
		// Create the user record
		
		Response res = withClient(KeycloakUtils.keycloakUrl("/admin/realms/" + realmId + "/users"),
				target -> target.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)
				.post(Entity.json(new KeycloakUserRegister(user))));
		
		hasStatus(res, Status.CREATED);
		
		return res;
	}
}
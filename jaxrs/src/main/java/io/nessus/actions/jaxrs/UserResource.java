package io.nessus.actions.jaxrs;

import static io.nessus.actions.core.utils.ApiUtils.hasStatus;

import java.net.URI;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.nessus.actions.core.jaxrs.AbstractUserResource;
import io.nessus.actions.core.service.KeycloakService;
import io.nessus.actions.core.types.KeycloakUserInfo;
import io.nessus.actions.core.utils.ApiUtils;
import io.nessus.actions.jaxrs.service.UserStateService;
import io.nessus.actions.jaxrs.type.UserState;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Path("/user")
@SecurityScheme(type = SecuritySchemeType.OPENIDCONNECT, scheme = "Bearer")
public class UserResource extends AbstractUserResource {
	
	// User State
	
	// GET http://localhost:8200/jaxrs/api/user/state
	// Authorization: "Bearer eyJhbGciOi..."
	
	@GET
	@Path("/{userId}/state")
	@Operation(summary = "Fetch the current state for the given user id")	           
	@ApiResponse(responseCode = "200", description = "[OK] Found the requested user state.",
		content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UserState.class)))
	@ApiResponse(responseCode = "401", description = "[Unauthorized] If the provided access token was not valid.")
	
	public Response getUserState(@PathParam("userId") String userId) {
		
		KeycloakUserInfo kcinfo = getKeycloakUserInfo(userId);
		if (kcinfo == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		UserStateService usrsvc = getService(UserStateService.class);
		UserState userState = usrsvc.getOrCreateUserState(kcinfo);
		
		return Response.ok(userState, MediaType.APPLICATION_JSON).build();
	}

	// User Delete
	
	// DELETE http://localhost:8200/jaxrs/api/user
	// Authorization: "Bearer eyJhbGciOi..."
	
	@DELETE
	@Path("/{userId}")
	@Operation(summary = "Delete the user with the given id")	           
	@ApiResponse(responseCode = "204", description = "[No Content] Sucessfully delete the user in Keycloak.")
	@ApiResponse(responseCode = "401", description = "[Unauthorized] If the provided access token was not valid.")
	
	public Response deleteUser(@PathParam("userId") String userId) {
		
		KeycloakUserInfo kcinfo = getKeycloakUserInfo(userId);
		if (kcinfo == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		// Delete the user
		
		String realmId = config.getKeycloakRealmId();
		KeycloakService keycloak = getKeycloakService();
		String masterToken = keycloak.getMasterAccessToken();
		URI uri = ApiUtils.keycloakUri(config, "/admin/realms/" + realmId + "/users/" + userId);
		Response res = withClient(uri, target -> target.request()
				.header("Authorization", "Bearer " + masterToken)
				.delete());
		
		hasStatus(res, Status.NO_CONTENT);
		
		return res;
	}
}
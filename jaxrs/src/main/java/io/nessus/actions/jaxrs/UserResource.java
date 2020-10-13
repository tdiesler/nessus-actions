package io.nessus.actions.jaxrs;

import static io.nessus.actions.jaxrs.utils.JaxrsUtils.hasStatus;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.nessus.actions.jaxrs.service.KeycloakService;
import io.nessus.actions.jaxrs.type.KeycloakUserInfo;
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
	
	@GET
	@Path("/{userId}/state")
	@Operation(summary = "Fetch the current state for the given user id")	           
	@ApiResponse(responseCode = "200", description = "[OK] Found the requested user state.",
		content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UserState.class)))
	@ApiResponse(responseCode = "401", description = "[Unauthorized] If the provided access token was not valid.")
	
	public Response userStatus(@PathParam("userId") String userId) {
		
		KeycloakUserInfo kcinfo = getKeycloakUserInfo(userId);
		if (kcinfo == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		UserState userStatus = new UserState(kcinfo);
		
		return Response.ok(userStatus, MediaType.APPLICATION_JSON).build();
	}

	@DELETE
	@Path("/{userId}")
	@Operation(summary = "Delete the user with the given id")	           
	@ApiResponse(responseCode = "204", description = "[No Content] Sucessfully delete the user in Keycloak.")
	@ApiResponse(responseCode = "401", description = "[Unauthorized] If the provided access token was not valid.")
	
	public Response userDelete(@PathParam("userId") String userId) {
		
		KeycloakUserInfo kcinfo = getKeycloakUserInfo(userId);
		if (kcinfo == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		// Delete the user
		
		String realmId = config.getRealmId();
		KeycloakService keycloak = getKeycloakService();
		String masterToken = keycloak.getMasterAccessToken();
		String url = keycloakUrl("/admin/realms/" + realmId + "/users/" + userId);
		Response res = withClient(url, target -> target.request()
				.header("Authorization", "Bearer " + masterToken)
				.delete());
		
		hasStatus(res, Status.NO_CONTENT);
		
		return res;
	}
}
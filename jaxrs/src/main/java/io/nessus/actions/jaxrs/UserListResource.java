package io.nessus.actions.jaxrs;

import static io.nessus.actions.core.utils.ApiUtils.hasStatus;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.nessus.actions.core.jaxrs.AbstractResource;
import io.nessus.actions.core.service.KeycloakService;
import io.nessus.actions.core.types.KeycloakTokens;
import io.nessus.actions.core.types.KeycloakUserInfo;
import io.nessus.actions.core.utils.ApiUtils;
import io.nessus.actions.jaxrs.service.UserStateService;
import io.nessus.actions.jaxrs.type.UserRegister;
import io.nessus.actions.jaxrs.type.UserTokens;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/users")
public class UserListResource extends AbstractResource {
	
	// User Register
	
	// PUT http://localhost:8200/jaxrs/api/users
	// 
	// {
	//	  "firstName": "My",
	//	  "lastName":  "User", 
	//	  "email":	   "myuser@example.com",
	//	  "username":  "myuser", 
	//	  "password":  "mypass"
	// }
	
	@PUT
	@Consumes(value = MediaType.APPLICATION_JSON)
	@Operation(summary = "Register a new user with Keycloak")	           
	@ApiResponse(responseCode = "201", description = "[Created] User was successfully created to Keycloak.")
	@ApiResponse(responseCode = "409", description = "[Conflict] If the user already exists in Keycloak.")
	
	public Response registerUser(UserRegister user) {
		
		logInfo("Register: {}", user.getEmail());
		
		String realmId = getConfig().getKeycloakRealmId();
		KeycloakService keycloak = getKeycloakService();
		String accessToken = keycloak.getMasterAccessToken();
		
		// Create the user record
		
		URI uri = ApiUtils.keycloakUri(config, "/admin/realms/" + realmId + "/users");
		Response res = withClient(uri, target -> target.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)
				.post(Entity.json(user.toKeycloakUserRegister())));
		
		hasStatus(res, Status.CREATED);
		
		return res;
	}

	// User Login
	
	// POST http://localhost:8200/jaxrs/api/user/token
	// Content-Type: application/x-www-form-urlencoded
	//
	// username: myuser 
	// password: mypass
	
	@POST
	@Path("/login")
	@Consumes(value = MediaType.APPLICATION_FORM_URLENCODED)
	@Operation(summary = "Login the user with Keycloak")	
	@ApiResponse(responseCode = "200", description = "[OK] Successful user login.",
		content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UserTokens.class)))
	@ApiResponse(responseCode = "401", description = "[Unauthorized] If the provided credentials were not valid.")
	
	public Response userLogin(
			@FormParam("username") @Parameter(required = true) String username, 
			@FormParam("password") @Parameter(required = true) String password) {
		
		logInfo("Login: {}", username);
		
		KeycloakService keycloak = getKeycloakService();
		Response res = keycloak.getUserTokens(username, password);
		
		if (!hasStatus(res, Status.OK)) {
			return res;
		}
		
		KeycloakTokens tokens = res.readEntity(KeycloakTokens.class);
		String refreshToken = tokens.refreshToken;
		String accessToken = tokens.accessToken;
		
		res = keycloak.getKeycloakUserInfo(accessToken);
		
		if (!hasStatus(res, Status.OK)) {
			return res;
		}
		
		KeycloakUserInfo kcinfo = res.readEntity(KeycloakUserInfo.class);
		UserTokens userTokens = new UserTokens(kcinfo.subject, accessToken, refreshToken);
		
		UserStateService usrsvc = getService(UserStateService.class);
		usrsvc.userLogin(kcinfo);
		
		return Response.ok(userTokens, MediaType.APPLICATION_JSON).build();
	}
}
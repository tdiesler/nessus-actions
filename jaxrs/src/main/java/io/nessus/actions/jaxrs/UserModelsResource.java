package io.nessus.actions.jaxrs;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.nessus.actions.jaxrs.service.UserModelsService;
import io.nessus.actions.jaxrs.type.KeycloakUserInfo;
import io.nessus.actions.jaxrs.type.UserModel;
import io.nessus.actions.jaxrs.type.UserModelAdd;
import io.nessus.actions.jaxrs.type.UserModels;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/user/{userId}/models")
public class UserModelsResource extends AbstractUserResource {
	
	@PUT
	@Consumes(value = MediaType.APPLICATION_JSON)
	@Operation(summary = "Add a new integration model")
	@ApiResponse(responseCode = "201", description = "[Created] Model was successfully added.", 
		content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UserModel.class)))
	@ApiResponse(responseCode = "401", description = "[Unauthorized] If the provided access token was not valid.")
	
	public Response addModel(@PathParam("userId") String userId, UserModelAdd modelAdd) {
		
		logInfo("Create model: {}", modelAdd);
		
		KeycloakUserInfo kcinfo = getKeycloakUserInfo(userId);
		if (kcinfo == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		if (!modelAdd.userId.equals(userId)) {
			logError("User id does not match");
			return null;
		}
		
		UserModelsService models = getService(UserModelsService.class);
		UserModel model = models.insertModel(modelAdd);
		
		return Response.status(Status.CREATED).type(MediaType.APPLICATION_JSON).entity(model).build();
	}

	@GET
	@Operation(summary = "Get the list of the given user's model ids.")
	@ApiResponse(responseCode = "200", description = "[OK] List of model ids.",
		content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UserModels.class)))
	@ApiResponse(responseCode = "401", description = "[Unauthorized] If the provided access token was not valid.")
	
	public Response findUserModels(@PathParam("userId") String userId) {
		
		logInfo("Get models for user: {}", userId);
		
		KeycloakUserInfo kcinfo = getKeycloakUserInfo(userId);
		if (kcinfo == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		UserModelsService mdlsrv = getService(UserModelsService.class);
		List<UserModel> models = mdlsrv.findUserModels(userId);
		
		UserModels userModelIds = new UserModels(userId, models);
		
		return Response.ok().type(MediaType.APPLICATION_JSON).entity(userModelIds).build();
	}
}
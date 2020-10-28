package io.nessus.actions.jaxrs;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.nessus.actions.core.jaxrs.AbstractUserResource;
import io.nessus.actions.core.types.KeycloakUserInfo;
import io.nessus.actions.jaxrs.service.UserModelsService;
import io.nessus.actions.jaxrs.type.UserModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Path("/user/{userId}/model")
@SecurityScheme(type = SecuritySchemeType.OPENIDCONNECT, scheme = "Bearer")
public class UserModelResource extends AbstractUserResource {
	
	@GET
	@Path("/{modelId}")
	@Operation(summary = "Get the model for the given id.")
	@ApiResponse(responseCode = "200", description = "[OK] Model for the given id could be accessed.",
		content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UserModel.class)))
	@ApiResponse(responseCode = "404", description = "[Not Found] The model for the given id could not be found.")
	@ApiResponse(responseCode = "401", description = "[Unauthorized] If the provided access token was not valid.")
	
	public Response getUserModel(@PathParam("userId") String userId, @PathParam("modelId") String modelId) {
		
		logInfo("Get model: {}", modelId);
		
		KeycloakUserInfo kcinfo = getKeycloakUserInfo(userId);
		if (kcinfo == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		UserModelsService models = getService(UserModelsService.class);
		UserModel model = models.findModel(userId, modelId);
		
		if (model == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		
		return Response.ok().type(MediaType.APPLICATION_JSON).entity(model).build();
	}
	
	@POST
	@Consumes(value = MediaType.APPLICATION_JSON)
	@Operation(summary = "Update the given model.")
	@ApiResponse(responseCode = "200", description = "[OK] Successfully updated the model.")
	@ApiResponse(responseCode = "401", description = "[Unauthorized] If the provided access token was not valid.")
	
	public Response updateUserModel(@PathParam("userId") String userId, UserModel userModel) {
		
		logInfo("Update model: {}", userModel.modelId);
		
		KeycloakUserInfo kcinfo = getKeycloakUserInfo(userId);
		if (kcinfo == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		UserModelsService models = getService(UserModelsService.class);
		models.updateModel(userModel);
		
		return Response.status(Status.OK).build();
	}
	
	@DELETE
	@Path("/{modelId}")
	@Operation(summary = "Delete the model with the given id.")
	@ApiResponse(responseCode = "204", description = "[No Content] Successfully deleted the model.")
	@ApiResponse(responseCode = "401", description = "[Unauthorized] If the provided access token was not valid.")
	
	public Response deleteUserModel(@PathParam("userId") String userId, @PathParam("modelId") String modelId) {
		
		logInfo("Delete model: {}", modelId);
		
		KeycloakUserInfo kcinfo = getKeycloakUserInfo(userId);
		if (kcinfo == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		UserModelsService models = getService(UserModelsService.class);
		models.deleteModel(userId, modelId);
		
		return Response.status(Status.NO_CONTENT).build();
	}
}
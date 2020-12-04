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

import io.nessus.actions.core.jaxrs.AbstractUserResource;
import io.nessus.actions.core.types.KeycloakUserInfo;
import io.nessus.actions.jaxrs.service.ModelService;
import io.nessus.actions.jaxrs.type.Model;
import io.nessus.actions.jaxrs.type.ModelAdd;
import io.nessus.actions.jaxrs.type.ModelList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/user/{userId}/models")
public class ModelListResource extends AbstractUserResource {
	
	// Create Model
	
	// PUT http://localhost:8200/jaxrs/api/user/{userId}/models
	// 
	// {
	//	  "userId": "myuser",
	//	  "content": "some model content", 
	// }
	
	@PUT
	@Consumes(value = MediaType.APPLICATION_JSON)
	@Operation(summary = "Create a new integration model")
	@ApiResponse(responseCode = "201", description = "[Created] Model was successfully created.", 
		content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Model.class)))
	@ApiResponse(responseCode = "401", description = "[Unauthorized] If the provided access token was not valid.")
	
	public Response createModel(@PathParam("userId") String userId, ModelAdd modelAdd) {
		
		logInfo("Create model: {}", modelAdd);
		
		KeycloakUserInfo kcinfo = getKeycloakUserInfo(userId);
		if (kcinfo == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		if (!modelAdd.userId.equals(userId)) {
			logError("User id does not match");
			return null;
		}
		
		ModelService mdlsrv = getService(ModelService.class);
		Model model = mdlsrv.createModel(modelAdd);
		
		return Response.status(Status.CREATED).type(MediaType.APPLICATION_JSON).entity(model).build();
	}

	// Get Models associated with a given user
	
	// GET http://localhost:8200/jaxrs/api/user/{userId}/models
	//
	
	@GET
	@Operation(summary = "Get the list models associated with a given user.")
	@ApiResponse(responseCode = "200", description = "[OK] List of user model definitions.",
		content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ModelList.class)))
	@ApiResponse(responseCode = "401", description = "[Unauthorized] If the provided access token was not valid.")
	
	public Response getUserModels(@PathParam("userId") String userId) {
		
		logInfo("Get models for user: {}", userId);
		
		KeycloakUserInfo kcinfo = getKeycloakUserInfo(userId);
		if (kcinfo == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		ModelService mdlsrv = getService(ModelService.class);
		List<Model> models = mdlsrv.findModels(userId);
		
		ModelList userModels = new ModelList(userId, models);
		
		return Response.ok().type(MediaType.APPLICATION_JSON).entity(userModels).build();
	}
}
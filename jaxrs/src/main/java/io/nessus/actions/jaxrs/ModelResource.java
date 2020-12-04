package io.nessus.actions.jaxrs;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Paths;

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
import io.nessus.actions.core.types.MavenBuildHandle;
import io.nessus.actions.core.types.MavenBuildHandle.BuildStatus;
import io.nessus.actions.core.utils.ApiUtils;
import io.nessus.actions.jaxrs.service.MavenBuilderService;
import io.nessus.actions.jaxrs.service.ModelService;
import io.nessus.actions.jaxrs.type.Model;
import io.nessus.actions.jaxrs.type.Model.TargetRuntime;
import io.nessus.common.AssertState;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Path("/user/{userId}/model")
@SecurityScheme(type = SecuritySchemeType.OPENIDCONNECT, scheme = "Bearer")
public class ModelResource extends AbstractUserResource {
	
	// Get Model
	
	// GET http://localhost:8200/jaxrs/api/user/{userId}/model/{modelId}
	//
	
	@GET
	@Path("/{modelId}")
	@Operation(summary = "Get the model for the given id.")
	@ApiResponse(responseCode = "200", description = "[OK] Model for the given id could be accessed.",
		content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Model.class)))
	@ApiResponse(responseCode = "401", description = "[Unauthorized] If the provided access token was not valid.")
	@ApiResponse(responseCode = "404", description = "[Not Found] The model for the given id could not be found.")
	
	public Response getModel(@PathParam("userId") String userId, @PathParam("modelId") String modelId) {
		
		logInfo("Get model: {}", modelId);
		
		KeycloakUserInfo kcinfo = getKeycloakUserInfo(userId);
		if (kcinfo == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		ModelService mdlsrv = getService(ModelService.class);
		Model model = mdlsrv.findModel(modelId);
		
		if (model == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		
		return Response.ok().type(MediaType.APPLICATION_JSON).entity(model).build();
	}
	
	// Update Model
	
	// POST http://localhost:8200/jaxrs/api/user/{userId}/model/{modelId}
	//
	
	@POST
	@Path("/{modelId}")
	@Consumes(value = MediaType.APPLICATION_JSON)
	@Operation(summary = "Update the given model.")
	@ApiResponse(responseCode = "200", description = "[OK] Successfully updated the model.")
	@ApiResponse(responseCode = "401", description = "[Unauthorized] If the provided access token was not valid.")
	
	public Response updateModel(@PathParam("userId") String userId, @PathParam("modelId") String modelId, Model model) {
		
		logInfo("Update model: {}", model.getModelId());
		
		KeycloakUserInfo kcinfo = getKeycloakUserInfo(userId);
		if (kcinfo == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		AssertState.isEqual(modelId, model.getModelId());
		
		ModelService mdlsrv = getService(ModelService.class);
		model = mdlsrv.updateModel(model);
		
		return Response.status(Status.OK).build();
	}
	
	// Delete Model
	
	// DELETE http://localhost:8200/jaxrs/api/user/{userId}/model/{modelId}
	//
	
	@DELETE
	@Path("/{modelId}")
	@Operation(summary = "Delete the model with the given id.")
	@ApiResponse(responseCode = "204", description = "[No Content] Successfully deleted the model.")
	@ApiResponse(responseCode = "401", description = "[Unauthorized] If the provided access token was not valid.")
	
	public Response deleteModel(@PathParam("userId") String userId, @PathParam("modelId") String modelId) {
		
		logInfo("Delete model: {}", modelId);
		
		KeycloakUserInfo kcinfo = getKeycloakUserInfo(userId);
		if (kcinfo == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		ModelService mdlsrv = getService(ModelService.class);
		mdlsrv.deleteModel(modelId);
		
		return Response.status(Status.NO_CONTENT).build();
	}

	// Build Model
	
	// GET http://localhost:8200/jaxrs/api/user/{userId}/model/{modelId}/{runtime}/build
	//
	
	@GET
	@Path("/{modelId}/{runtime}/build")
	@Operation(summary = "Schedule the build process for the model.")
	@ApiResponse(responseCode = "200", description = "[OK] Successfully scheduled the model build.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = MavenBuildHandle.class)))
	@ApiResponse(responseCode = "401", description = "[Unauthorized] If the provided access token was not valid.")
	@ApiResponse(responseCode = "404", description = "[Not Found] The model for the given id could not be found.")
	
	public Response buildModel(@PathParam("userId") String userId, @PathParam("modelId") String modelId, @PathParam("runtime") TargetRuntime runtime) {

		logInfo("Schedule model build: {}", modelId);
		
		KeycloakUserInfo kcinfo = getKeycloakUserInfo(userId);
		if (kcinfo == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}

		ModelService mdlsrv = getService(ModelService.class);
		Model model = mdlsrv.findModel(modelId);
		if (model == null) {
			return Response.status(Status.NOT_FOUND).build();
		}

		MavenBuilderService maven = getService(MavenBuilderService.class);
		Response res = maven.buildModelWithMaven(kcinfo.username, model, runtime);
		
		if (!ApiUtils.hasStatus(res, Status.OK))
			return res;
		
		MavenBuildHandle handle = res.readEntity(MavenBuildHandle.class);
		return Response.ok(handle, MediaType.APPLICATION_JSON).build();
	}

	// Get Build Status
	
	// GET http://localhost:8200/jaxrs/api/user/{userId}/model/{modelId}/{runtime}/status
	//
	
	@GET
	@Path("/{modelId}/{runtime}/status")
	@Operation(summary = "Get the current build status")	
	@ApiResponse(responseCode = "200", description = "[OK] Found the status for the requested project.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = MavenBuildHandle.class)))
	@ApiResponse(responseCode = "401", description = "[Unauthorized] If the provided credentials were not valid.")
	@ApiResponse(responseCode = "404", description = "[Not Found] The project for the given id was not found.")
	
	public Response getBuildStatus(@PathParam("userId") String userId, @PathParam("modelId") String modelId, @PathParam("runtime") TargetRuntime runtime) {

		KeycloakUserInfo kcinfo = getKeycloakUserInfo(userId);
		if (kcinfo == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}

		ModelService mdlsrv = getService(ModelService.class);
		Model model = mdlsrv.findModel(modelId);
		if (model == null) {
			return Response.status(Status.NOT_FOUND).build();
		}

		MavenBuilderService maven = getService(MavenBuilderService.class);
		Response res = maven.getModelBuildStatus(kcinfo.username, model, runtime);
		
		if (!ApiUtils.hasStatus(res, Status.OK))
			return res;
		
		MavenBuildHandle handle = res.readEntity(MavenBuildHandle.class);
		return Response.ok(handle, MediaType.APPLICATION_JSON).build();
	}

	// Download the Target File
	
	// GET http://localhost:8200/jaxrs/api/user/{userId}/model/{modelId}/{runtime}/download
	//
	
	@GET
	@Path("/{modelId}/{runtime}/download")
	@Operation(summary = "Download the build target")	
	@ApiResponse(responseCode = "200", description = "[OK] Found the requested build target.",
			content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM))
	@ApiResponse(responseCode = "401", description = "[Unauthorized] If the provided credentials were not valid.")
	@ApiResponse(responseCode = "404", description = "[Not Found] The target file was not found.")
	
	public Response downloadBuildTarget(@PathParam("userId") String userId, @PathParam("modelId") String modelId, @PathParam("runtime") TargetRuntime runtime) {

		KeycloakUserInfo kcinfo = getKeycloakUserInfo(userId);
		if (kcinfo == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}

		ModelService mdlsrv = getService(ModelService.class);
		Model model = mdlsrv.findModel(modelId);
		if (model == null) {
			return Response.status(Status.NOT_FOUND).build();
		}

		MavenBuilderService maven = getService(MavenBuilderService.class);
		Response res = maven.getModelBuildStatus(kcinfo.username, model, runtime);
		
		if (!ApiUtils.hasStatus(res, Status.OK))
			return res;
		
		MavenBuildHandle handle = res.readEntity(MavenBuildHandle.class);
		if (handle.getBuildStatus() != BuildStatus.Success) {
			return Response.status(Status.NOT_FOUND).build();
		}
		
		res = maven.getModelTargetDownload(kcinfo.username, model, runtime);
		
		if (!ApiUtils.hasStatus(res, Status.OK))
			return res;
		
		File targetFile = Paths.get(handle.getLocation()).toFile();
		InputStream inputStream = res.readEntity(InputStream.class);
		res = Response.ok(inputStream)
				.header("Content-Disposition", "attachment;filename=" + targetFile.getName())
				.build();
		
		return res;
	}
}
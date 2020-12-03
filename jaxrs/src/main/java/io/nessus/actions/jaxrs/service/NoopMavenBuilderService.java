package io.nessus.actions.jaxrs.service;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.nessus.actions.core.NessusConfig;
import io.nessus.actions.jaxrs.type.UserModel;

public class NoopMavenBuilderService extends AbstractMavenBuilderService {

	public NoopMavenBuilderService(NessusConfig config) {
		super(config);
	}

	@Override
	public Response buildModelWithMaven(String username, UserModel userModel, String runtime) {
		return Response.status(Status.OK).build();
	}

	@Override
	public Response getModelBuildStatus(String username, UserModel userModel, String runtime) {
		return Response.status(Status.OK).build();
	}

	@Override
	public Response getModelTargetDownload(String username, UserModel userModel, String runtime) {
		return Response.status(Status.OK).build();
	}
}
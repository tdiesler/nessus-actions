package io.nessus.actions.jaxrs.service;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.nessus.actions.core.NessusConfig;
import io.nessus.actions.core.types.KeycloakUserInfo;
import io.nessus.actions.jaxrs.type.UserModel;

public class NoopMavenBuilderService extends AbstractMavenBuilderService {

	public NoopMavenBuilderService(NessusConfig config) {
		super(config);
	}

	@Override
	public Response buildModelWithMaven(KeycloakUserInfo kcinfo, UserModel userModel, String runtime) {
		
		return Response.status(Status.OK).build();
	}
}
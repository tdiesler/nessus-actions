package io.nessus.actions.jaxrs.service;

import javax.ws.rs.core.Response;

import io.nessus.actions.jaxrs.type.UserModel;
import io.nessus.common.service.Service;

public interface MavenBuilderService extends Service {

	Response buildModelWithMaven(String username, UserModel userModel, String runtime);

	Response getModelBuildStatus(String username, UserModel userModel, String runtime);

}
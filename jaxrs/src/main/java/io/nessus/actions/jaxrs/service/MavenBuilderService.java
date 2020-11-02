package io.nessus.actions.jaxrs.service;

import javax.ws.rs.core.Response;

import io.nessus.actions.core.types.KeycloakUserInfo;
import io.nessus.actions.jaxrs.type.UserModel;
import io.nessus.common.service.Service;

public interface MavenBuilderService extends Service {

	Response buildModelWithMaven(KeycloakUserInfo kcinfo, UserModel userModel, String runtime);

}
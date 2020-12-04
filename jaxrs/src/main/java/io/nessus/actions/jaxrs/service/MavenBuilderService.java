package io.nessus.actions.jaxrs.service;

import javax.ws.rs.core.Response;

import io.nessus.actions.jaxrs.type.Model;
import io.nessus.actions.jaxrs.type.Model.TargetRuntime;
import io.nessus.common.service.Service;

public interface MavenBuilderService extends Service {

	Response buildModelWithMaven(String username, Model model, TargetRuntime runtime);

	Response getModelBuildStatus(String username, Model model, TargetRuntime runtime);

	Response getModelTargetDownload(String username, Model model, TargetRuntime runtime);
}
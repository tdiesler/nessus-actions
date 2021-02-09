package io.nessus.actions.jaxrs.service;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Date;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;

import io.nessus.actions.core.NessusConfig;
import io.nessus.actions.core.maven.MavenProjectBuilder;
import io.nessus.actions.core.types.MavenBuildHandle;
import io.nessus.actions.core.types.MavenBuildHandle.BuildStatus;
import io.nessus.actions.core.utils.ApiUtils;
import io.nessus.actions.jaxrs.type.Model;
import io.nessus.actions.jaxrs.type.Model.ModelState;
import io.nessus.actions.jaxrs.type.Model.TargetRuntime;
import io.nessus.common.CheckedExceptionWrapper;
import io.nessus.common.utils.DateUtils;

public class DefaultMavenBuilderService extends AbstractMavenBuilderService {

	public DefaultMavenBuilderService(NessusConfig config) {
		super(config);
	}

	@Override
	public Response buildModelWithMaven(String username, Model model, TargetRuntime runtime) {
		
		String groupId = "io." + username;
		String artifactId = model.getTitle().replace(' ', '-').toLowerCase();
		
		try {
			
			// Generate the maven project
			
	        URL regurl = new URL(config.getRegistryUrl());
			String regHostPort = regurl.getHost() + ":" + regurl.getPort();
			
			URL zipurl = new MavenProjectBuilder(String.format("%s:%s:1.0.0", groupId, artifactId))
					.routeModel(model.getRouteModel())
					.property("quarkus.container-image.registry", regHostPort)
					.property("quarkus.container-image.insecure", "true")
					.property("quarkus.container-image.build", "true")
					.property("quarkus.container-image.push", "true")
					.generate().assemble(runtime.toString()).toURL();
			
			// Schedule the maven build 
			
			InputStream projZip = zipurl.openStream();
			
			String modelId = model.getModelId();
			String projId = modelId + "/" + runtime;
			MultipartFormDataOutput formData = new MultipartFormDataOutput();
			formData.addFormData("projId", projId, MediaType.TEXT_PLAIN_TYPE);
			formData.addFormData("projZip", projZip, MediaType.APPLICATION_OCTET_STREAM_TYPE);
			GenericEntity<MultipartFormDataOutput> entity = new GenericEntity<MultipartFormDataOutput>(formData) { };
			
			Date startTime = new Date();
			
			URI uri = ApiUtils.mavenUri(getConfig(), "/api/build/schedule");
			Response res = withClient(uri, target -> target.request()
					.post(Entity.entity(entity, MediaType.MULTIPART_FORM_DATA_TYPE)));
			
			res.bufferEntity();
			
			ModelService mdlsrv = getService(ModelService.class);
			MavenBuildHandle handle = res.readEntity(MavenBuildHandle.class);
			mdlsrv.updateModelState(model, new ModelState(runtime, handle.getBuildStatus()));
			
			if (ApiUtils.hasStatus(res, Status.OK)) {
				new Thread(() -> {
					
					BuildStatus buildStatus = handle.getBuildStatus();
					logInfo("{} => {} {}", handle.getId(), buildStatus, DateUtils.elapsedTimeString(startTime));
					
					Client client = ClientBuilder.newClient();
					try {
						
						while (buildStatus == BuildStatus.Scheduled || buildStatus == BuildStatus.Running) {
							
							sleepSafe(2500);
							
							URI auxuri = ApiUtils.mavenUri(getConfig(), "/api/build/" + projId + "/status");
							Response auxres = client.target(auxuri).request().get();
							
							ApiUtils.assertStatus(auxres, Status.OK);
							
							buildStatus = auxres.readEntity(MavenBuildHandle.class).getBuildStatus();
							mdlsrv.updateModelState(model, new ModelState(runtime, buildStatus));
							
							logInfo("{} => {} {}", handle.getId(), buildStatus, DateUtils.elapsedTimeString(startTime));
						}
						
					} finally {
						client.close();
					}
					
				}, "BuildStatusMonitor").start();
			}
			
			return res;
			
		} catch (Exception ex) {
			throw CheckedExceptionWrapper.create(ex);
		}
	}

	@Override
	public Response getModelBuildStatus(String username, Model model, TargetRuntime runtime) {
		
		String majorId = model.getModelId();
		String projId = majorId + "/" + runtime;
		
		URI uri = ApiUtils.mavenUri(getConfig(), "/api/build/" + projId + "/status");
		Response res = withClient(uri, target -> target.request().get());
		
		return res;
	}

	@Override
	public Response getModelTargetDownload(String username, Model model, TargetRuntime runtime) {
		
		String majorId = model.getModelId();
		String projId = majorId + "/" + runtime;
		
		URI uri = ApiUtils.mavenUri(getConfig(), "/api/build/" + projId + "/download");
		Response res = withClient(uri, target -> target.request().get());
		
		return res;
	}

	@Override
	public Response getModelProjectSources(String username, Model model, TargetRuntime runtime) {
		
		String majorId = model.getModelId();
		String projId = majorId + "/" + runtime;
		
		URI uri = ApiUtils.mavenUri(getConfig(), "/api/build/" + projId + "/sources");
		Response res = withClient(uri, target -> target.request().get());
		
		return res;
	}
}
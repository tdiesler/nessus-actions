package io.nessus.actions.jaxrs.service;

import java.io.InputStream;
import java.net.URI;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;

import io.nessus.actions.core.NessusConfig;
import io.nessus.actions.core.maven.MavenProjectBuilder;
import io.nessus.actions.core.types.KeycloakUserInfo;
import io.nessus.actions.core.utils.ApiUtils;
import io.nessus.actions.jaxrs.type.UserModel;
import io.nessus.common.CheckedExceptionWrapper;

public class DefaultMavenBuilderService extends AbstractMavenBuilderService {

	public DefaultMavenBuilderService(NessusConfig config) {
		super(config);
	}

	@Override
	public Response buildModelWithMaven(KeycloakUserInfo kcinfo, UserModel userModel, String runtime) {
		
		String groupId = "io." + kcinfo.username;
		String artifactId = userModel.getTitle().replace(' ', '-').toLowerCase();
		
		try {
			
			// Generate the maven project 
			
			URI uri = new MavenProjectBuilder(String.format("%s:%s:1.0.0", groupId, artifactId))
					.routeModel(userModel.getRouteModel()).generate().assemble();
			
			// Schedule the maven build 
			
			InputStream projZip = uri.toURL().openStream();
			
			String modelId = userModel.getModelId();
			MultipartFormDataOutput formData = new MultipartFormDataOutput();
			formData.addFormData("projId", modelId + "/" + runtime, MediaType.TEXT_PLAIN_TYPE);
			formData.addFormData("projZip", projZip, MediaType.APPLICATION_OCTET_STREAM_TYPE);
			GenericEntity<MultipartFormDataOutput> entity = new GenericEntity<MultipartFormDataOutput>(formData) { };
			
			uri = ApiUtils.mavenUri(getConfig(), "/api/build/schedule");
			Response res = ClientBuilder.newClient().target(uri).request()
				.post(Entity.entity(entity, MediaType.MULTIPART_FORM_DATA_TYPE));
			
			return res;
			
		} catch (Exception ex) {
			throw CheckedExceptionWrapper.create(ex);
		}
	}
}
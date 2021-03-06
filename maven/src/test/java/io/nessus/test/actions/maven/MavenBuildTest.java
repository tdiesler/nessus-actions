/*-
 * #%L
 * Nessus :: Weka :: API
 * %%
 * Copyright (C) 2020 Nessus
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.nessus.test.actions.maven;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.junit.Assert;
import org.junit.Test;

import io.nessus.actions.core.maven.MavenProjectBuilder;
import io.nessus.actions.core.types.MavenBuildHandle;
import io.nessus.actions.core.types.MavenBuildHandle.BuildStatus;
import io.nessus.actions.core.utils.ApiUtils;
import io.nessus.common.utils.StreamUtils;

public class MavenBuildTest extends AbstractMavenTest {

	Client client = ClientBuilder.newClient();

	@Test
	public void testBuildProject() throws Exception {

		// Generate the Maven project from the given route model
		
		String runtime = "standalone";
		URI zipurl = new MavenProjectBuilder("org.acme.ticker:acme-ticker:1.0.0")
				.routeModelFromClasspath("/crypto-ticker.yaml")
				.property("quarkus.container-image.build", "false")
				.property("quarkus.container-image.push", "false")
				.generate().assemble(runtime);

		// Verify maven project content
		
		final String projName = zipurl.getPath().substring(zipurl.getPath().lastIndexOf('/') + 1);
		Assert.assertEquals("acme-ticker-1.0.0-project.tgz", projName);

		GenericArchive archive = ShrinkWrap.create(ZipImporter.class, projName)
				.importFrom(new File(zipurl))
				.as(GenericArchive.class);
		
		Assert.assertNotNull(archive.get("standalone/src/main/resources/camel-route-model.yaml"));
		Assert.assertNotNull(archive.get("standalone/pom.xml"));

		// Schedule Maven Build
		
		// POST http://localhost:8100/maven/api/build/schedule
		// 
		
		String modelId = "1234-5678-0000-0000";
		String projId = modelId + "/" + runtime;
		
		InputStream projZip = archive.as(ZipExporter.class)
				.exportAsInputStream();
		
		MultipartFormDataOutput formData = new MultipartFormDataOutput();
		formData.addFormData("projId", projId, MediaType.TEXT_PLAIN_TYPE);
		formData.addFormData("projZip", projZip, MediaType.APPLICATION_OCTET_STREAM_TYPE);
		GenericEntity<MultipartFormDataOutput> entity = new GenericEntity<MultipartFormDataOutput>(formData) { };
		
		URI uri = ApiUtils.mavenUri(getConfig(), "/api/build/schedule");
		Response res = client.target(uri).request()
			.post(Entity.entity(entity, MediaType.MULTIPART_FORM_DATA_TYPE));
		
		ApiUtils.hasStatus(res, Status.OK);
		
		MavenBuildHandle handle = res.readEntity(MavenBuildHandle.class);
		BuildStatus buildStatus = handle.getBuildStatus();

		Assert.assertTrue(new File(handle.getBuildSources()).isFile());
		Assert.assertEquals(BuildStatus.Scheduled, buildStatus);
		
		// Get Build Status
		
		// GET http://localhost:8100/maven/api/build/{projId}/status
		//
		
		uri = ApiUtils.mavenUri(getConfig(), "/api/build/" + projId + "/status");
		
		String lastLogMsg = null;
		while (buildStatus != BuildStatus.Success && buildStatus != BuildStatus.Failure) {
			
			sleepSafe(500);
			
			res = client.target(uri).request().get();
			
			ApiUtils.hasStatus(res, Status.OK);
			
			handle = res.readEntity(MavenBuildHandle.class);
			buildStatus = handle.getBuildStatus();
			
			String logMsg = String.format("%s => %s", handle.getId(), buildStatus);
			if (!logMsg.equals(lastLogMsg)) {
				logInfo("{}", logMsg);
				lastLogMsg = logMsg;
			}
		}
		
		Assert.assertEquals(BuildStatus.Success, buildStatus);
		
		// Download the Target File
		
		// GET http://localhost:8100/maven/api/build/{projId}/download
		//
		
		uri = ApiUtils.mavenUri(getConfig(), "/api/build/" + projId + "/download");
		
		res = client.target(uri).request().get();
		
		ApiUtils.hasStatus(res, Status.OK);
		
		String contentDisposition = res.getHeaderString("Content-Disposition");
		Assert.assertTrue(contentDisposition.startsWith("attachment;filename="));

		int fnameIdx = contentDisposition.indexOf('=') + 1;
		String fileName = contentDisposition.substring(fnameIdx);
		File targetFile = new File("target/" + fileName);
		
		InputStream ins = res.readEntity(InputStream.class);
		try (FileOutputStream fos = new FileOutputStream(targetFile)) {
			StreamUtils.copyStream(ins, fos);
		}
		
		logInfo("Downloaded: {}", targetFile);
		
		Assert.assertTrue(targetFile.isFile());
		
		// Download the project sources
		
		// GET http://localhost:8100/maven/api/build/{projId}/sources
		//
		
		uri = ApiUtils.mavenUri(getConfig(), "/api/build/" + projId + "/sources");
		
		res = client.target(uri).request().get();
		
		ApiUtils.hasStatus(res, Status.OK);
		
		contentDisposition = res.getHeaderString("Content-Disposition");
		Assert.assertTrue(contentDisposition.startsWith("attachment;filename="));

		fnameIdx = contentDisposition.indexOf('=') + 1;
		fileName = contentDisposition.substring(fnameIdx);
		targetFile = new File("target/" + fileName);
		
		ins = res.readEntity(InputStream.class);
		try (FileOutputStream fos = new FileOutputStream(targetFile)) {
			StreamUtils.copyStream(ins, fos);
		}
		
		logInfo("Downloaded: {}", targetFile);
		
		Assert.assertTrue(targetFile.isFile());
	}
}
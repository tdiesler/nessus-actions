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
import java.io.InputStream;
import java.net.URI;

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
import io.nessus.actions.core.utils.ApiUtils;
import io.nessus.actions.maven.MavenBuildHandle;

public class MavenProjectBuilderTest extends AbstractMavenTest {

	@Test
	public void testBuildProject() throws Exception {

		// Generate the Maven project from the given route model
		
		URI uri = new MavenProjectBuilder("org.acme.ticker:acme-ticker:1.0.0")
				.routeModelFromClasspath("/crypto-ticker.yaml").generate().assemble();

		// Verify maven project content
		
		String projName = uri.getPath().substring(uri.getPath().lastIndexOf('/') + 1);
		Assert.assertEquals("acme-ticker-1.0.0-project.tgz", projName);

		projName = projName.substring(0, projName.length() - 4);
		GenericArchive archive = ShrinkWrap.create(ZipImporter.class, projName)
				.importFrom(new File(uri))
				.as(GenericArchive.class);
		
		Assert.assertNotNull(archive.get("src/main/resources/camel-route-model.yaml"));
		Assert.assertNotNull(archive.get("pom.xml"));

		// Schedule the maven build 
		
		InputStream projZip = archive.as(ZipExporter.class)
				.exportAsInputStream();
		
		MultipartFormDataOutput formData = new MultipartFormDataOutput();
		formData.addFormData("projName", projName, MediaType.TEXT_PLAIN_TYPE);
		formData.addFormData("projZip", projZip, MediaType.APPLICATION_OCTET_STREAM_TYPE);
		GenericEntity<MultipartFormDataOutput> entity = new GenericEntity<MultipartFormDataOutput>(formData) { };
		
		String url = ApiUtils.mavenUrl(getConfig(), "/api/build/schedule");
		Response res = ClientBuilder.newClient().target(url).request()
			.post(Entity.entity(entity, MediaType.MULTIPART_FORM_DATA_TYPE));
		
		ApiUtils.hasStatus(res, Status.OK);
		
		MavenBuildHandle buildHandle = res.readEntity(MavenBuildHandle.class);
		Assert.assertTrue(new File(buildHandle.getHandle()).isFile());
	}
}
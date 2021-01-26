/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.nessus.test.actions.core;

import java.io.File;
import java.net.URI;

import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.junit.Assert;
import org.junit.Test;

import io.nessus.actions.core.maven.MavenProjectBuilder;

public class MavenProjectBuilderTest {

	@Test
	public void buildMavenProject() throws Exception {
    	
		URI zipurl = new MavenProjectBuilder("org.acme.ticker:acme-ticker:1.0.0")
			.routeModelFromClasspath("/model/crypto-ticker.yaml")
			.generate()
			.assemble();
		
		String name = zipurl.getPath().substring(zipurl.getPath().lastIndexOf('/') + 1);
		Assert.assertEquals("acme-ticker-1.0.0-project.tgz", name);
		
		GenericArchive archive = ShrinkWrap.create(ZipImporter.class, name)
			.importFrom(new File(zipurl))
			.as(GenericArchive.class);
	
		Assert.assertNotNull(archive.get("src/main/java/io/nessus/actions/quarkus/ModelRoutes.java"));
		Assert.assertNotNull(archive.get("src/main/resources/camel-route-model.yaml"));
		Assert.assertNotNull(archive.get("src/main/resources/application.properties"));
		Assert.assertNotNull(archive.get("pom.xml"));
	}
}

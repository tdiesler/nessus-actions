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
package io.nessus.actions.quarkus.maven;

import static io.nessus.actions.core.model.RouteModel.CAMEL_ROUTE_MODEL_RESOURCE_NAME;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;

import io.nessus.actions.core.model.ModelRouteBuilder;
import io.nessus.actions.core.model.RouteModel;
import io.nessus.actions.core.model.ToStep;
import io.nessus.actions.core.utils.IOUtils;
import io.nessus.common.AssertState;
import io.nessus.common.CheckedExceptionWrapper;

public class MavenProjectBuilder {
	
	private Path targetDir = Paths.get("target/generated-project");
	private MavenProject project = new MavenProject();
	private RouteModel routeModel;
	
	public MavenProjectBuilder(String coords) {
		Artifact art = new DefaultArtifact(coords);
		project.setGroupId(art.getGroupId());
		project.setArtifactId(art.getArtifactId());
		project.setVersion(art.getVersion());
	}

	public MavenProjectBuilder setGroupId(String groupId) {
		project.setGroupId(groupId);
		return this;
	}
	
	public MavenProjectBuilder setArtifactId(String artifactId) {
		project.setArtifactId(artifactId);
		return this;
	}
	
	public MavenProjectBuilder setVersion(String version) {
		project.setVersion(version);
		return this;
	}
	
	public MavenProjectBuilder addDependency(String coords) {
		List<Dependency> deps = project.getDependencies();
		Artifact art = new DefaultArtifact(coords);
		Dependency dep = new Dependency();
		dep.setGroupId(art.getGroupId());
		dep.setArtifactId(art.getArtifactId());
		dep.setVersion(art.getVersion());
		deps.add(dep);
		return this;
	}
	
	public MavenProjectBuilder setRouteModel(String modelResource) {
		try {
			this.routeModel = new ModelRouteBuilder()
				.withModelResource(modelResource)
				.loadModel();
		} catch (IOException ex) {
			throw CheckedExceptionWrapper.create(ex);
		}
		return this;
	}
	
	public MavenProjectBuilder setRouteModel(RouteModel routeModel) {
		this.routeModel = routeModel;
		return this;
	}
	
	public MavenProjectBuilder setTargetDir(Path targetDir) {
		this.targetDir = targetDir;
		return this;
	}
	
	public void build() throws Exception {
		
		AssertState.notNull(routeModel, "Null routeModel");
		
        // Read template and pass through velocity
		
		String tmplPath = "/templates/maven-project.vm";
		InputStream tmplIn = getClass().getResourceAsStream(tmplPath);
		AssertState.notNull(tmplIn, "Null resource: " + tmplPath);
		
        VelocityContext context = new VelocityContext();
        context.put("project", project);
        
		Path mainResources = targetDir.resolve("src/main/resources");
		mainResources.toFile().mkdirs();
		
		// Add route model dependencies
		
		routeModel.getSteps().stream()
			.filter(st -> st instanceof ToStep)
			.forEach(st -> {
				ToStep toStep = (ToStep) st;
				String comp = toStep.getComp();
				if (comp.startsWith("camel/")) {
					comp = comp.substring(6, comp.indexOf('@'));
					String groupId = "org.apache.camel.quarkus";
					addDependency(String.format("%s:camel-quarkus-%s:0.0.0", groupId, comp));
				}
			});

		// Write the pom.xml
		
		File pomFile = targetDir.resolve("pom.xml").toFile();
		try (Writer out = new OutputStreamWriter(new FileOutputStream(pomFile))) {
			try (InputStreamReader reader = new InputStreamReader(tmplIn)) {
	            Velocity.evaluate(context, out, tmplPath, reader);
	        }
		}
		
		// Write the camel-route-model.yaml
		
		File routeModelFile = mainResources.resolve(CAMEL_ROUTE_MODEL_RESOURCE_NAME).toFile();
		try (OutputStream fos = new FileOutputStream(routeModelFile)) {
			ByteArrayInputStream ins = new ByteArrayInputStream(routeModel.toString().getBytes());
			IOUtils.copyStream(ins, fos);
		}
		
		// Write static sources
		
		List<String> resources = Arrays.asList(
			"src/main/java/io/nessus/actions/quarkus/ModelRoutes.java",
			"src/main/resources/application.properties");
		
		for (String res : resources) {
			InputStream ins = getClass().getResourceAsStream("/etc/" + res);
			File outFile = targetDir.resolve(res).toFile();
			outFile.getParentFile().mkdirs();
			try (OutputStream fos = new FileOutputStream(outFile)) {
				IOUtils.copyStream(ins, fos);
			}
		}
	}
}

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
package io.nessus.actions.core.maven;

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
import java.net.URI;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;

import io.nessus.actions.core.model.RouteModel;
import io.nessus.actions.core.model.ToStep;
import io.nessus.common.AssertState;
import io.nessus.common.CheckedExceptionWrapper;
import io.nessus.common.utils.FileUtils;
import io.nessus.common.utils.StreamUtils;

/**
 * Builds a maven project tree from the given configuration
 * 
 * @author tdiesler@redhat.com
 */
public class MavenProjectBuilder {
	
	private final MavenProject project = new MavenProject();
	private RouteModel routeModel;
	
	public MavenProjectBuilder(String coords) {
		Artifact art = new DefaultArtifact(coords);
		groupId(art.getGroupId());
		artifactId(art.getArtifactId());
		version(art.getVersion());
		
		targetBaseDir(Paths.get("target/generated-project"));
	}
	
	public MavenProjectBuilder targetBaseDir(Path basedir) {
		project.setFile(basedir.resolve("pom.xml").toFile());
		return this;
	}

	public MavenProjectBuilder groupId(String groupId) {
		project.setGroupId(groupId);
		return this;
	}
	
	public MavenProjectBuilder artifactId(String artifactId) {
		project.setArtifactId(artifactId);
		return this;
	}
	
	public MavenProjectBuilder version(String version) {
		project.setVersion(version);
		return this;
	}
	
	public MavenProjectBuilder dependency(String coords) {
		List<Dependency> deps = project.getDependencies();
		Artifact art = new DefaultArtifact(coords);
		Dependency dep = new Dependency();
		dep.setGroupId(art.getGroupId());
		dep.setArtifactId(art.getArtifactId());
		dep.setVersion(art.getVersion());
		deps.add(dep);
		return this;
	}
	
	public MavenProjectBuilder routeModelFromClasspath(String modelResource) {
		try {
			URL resurl = getClass().getResource(modelResource);
			AssertState.notNull(resurl, "Cannot find: " + modelResource);
			this.routeModel = RouteModel.read(resurl);
		} catch (IOException ex) {
			throw CheckedExceptionWrapper.create(ex);
		}
		return this;
	}
	
	public MavenProjectBuilder routeModel(RouteModel routeModel) {
		this.routeModel = routeModel;
		return this;
	}
	
	public MavenProject getMavenProject() {
		return project;
	}

	public MavenProjectBuilder generate() throws Exception {
		
		AssertState.notNull(routeModel, "Null routeModel");
		
        Path basedir = project.getBasedir().toPath();
        FileUtils.recursiveDelete(basedir);
        
        // Read template and pass through velocity
		
		String tmplPath = "/templates/maven-project.vm";
		InputStream tmplIn = getClass().getResourceAsStream(tmplPath);
		AssertState.notNull(tmplIn, "Null resource: " + tmplPath);
		
        VelocityContext context = new VelocityContext();
        context.put("project", project);
        
		Path mainResources = basedir.resolve("src/main/resources");
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
					dependency(String.format("%s:camel-quarkus-%s:0.0.0", groupId, comp));
				}
			});

		// Write the pom.xml
		
		File pomFile = project.getFile();
		try (Writer out = new OutputStreamWriter(new FileOutputStream(pomFile))) {
			try (InputStreamReader reader = new InputStreamReader(tmplIn)) {
	            Velocity.evaluate(context, out, tmplPath, reader);
	        }
		}
		
		// Write the camel-route-model.yaml
		
		File routeModelFile = mainResources.resolve(CAMEL_ROUTE_MODEL_RESOURCE_NAME).toFile();
		try (OutputStream fos = new FileOutputStream(routeModelFile)) {
			ByteArrayInputStream ins = new ByteArrayInputStream(routeModel.toString().getBytes());
			StreamUtils.copyStream(ins, fos);
		}
		
		// Write static sources
		
		List<String> classpathResources = Arrays.asList(
			"src/main/java/io/nessus/actions/quarkus/ModelRoutes.java",
			"src/main/resources/application.properties");
		
		for (String res : classpathResources) {
			InputStream ins = getClass().getResourceAsStream("/etc/" + res);
			File outFile = basedir.resolve(res).toFile();
			outFile.getParentFile().mkdirs();
			try (OutputStream fos = new FileOutputStream(outFile)) {
				StreamUtils.copyStream(ins, fos);
			}
		}
		
		return this;
	}
	
	public URI assemble() throws IOException {
		
		String artifactId = project.getArtifactId();
		String version = project.getVersion();
		String zipName = String.format("%s-%s-project.tgz", artifactId, version);
		
        Path basedir = project.getBasedir().toPath().toAbsolutePath();
		Path zipPath = basedir.getParent().resolve(zipName);
		
		GenericArchive archive = ShrinkWrap.create(GenericArchive.class);
		
		Files.walkFileTree(basedir, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
				Asset asset = new FileAsset(path.toFile());
				Path target = basedir.relativize(path);
				archive.add(asset, target.toString());
		        return FileVisitResult.CONTINUE;
			}
		});
		
		archive.as(ZipExporter.class)
			.exportTo(zipPath.toFile(), true);
		
		return zipPath.toUri();
	}
}
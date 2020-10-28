package io.nessus.actions.maven;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.nessus.actions.core.NessusConfig;
import io.nessus.actions.core.jaxrs.AbstractApplication;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
	info = @Info(title = "Fuse TryIt - Maven API", version = "1.0.0"), 
	externalDocs = @ExternalDocumentation(url = "https://github.com/tdiesler/nessus-actions"))
public class MavenApplication extends AbstractApplication {
	
	public MavenApplication(NessusConfig config) {
		super(config);
	}
	
	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<>();
		classes.add(MavenBuildResource.class);
		return Collections.unmodifiableSet(classes);
	}
}
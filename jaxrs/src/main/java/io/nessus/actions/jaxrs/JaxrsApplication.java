package io.nessus.actions.jaxrs;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.nessus.actions.core.NessusConfig;
import io.nessus.actions.core.jaxrs.AbstractApplication;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
	info = @Info(title = "Fuse TryIt - Jaxrs API", version = "1.0.0"), 
	externalDocs = @ExternalDocumentation(url = "https://github.com/tdiesler/nessus-actions"))
public class JaxrsApplication extends AbstractApplication {
	
	public JaxrsApplication(NessusConfig config) {
		super(config);
	}
	
	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<>();
		classes.add(UserModelResource.class);
		classes.add(UserModelsResource.class);
		classes.add(UsersResource.class);
		classes.add(UserResource.class);
		return Collections.unmodifiableSet(classes);
	}
}
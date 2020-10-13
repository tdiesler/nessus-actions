package io.nessus.actions.jaxrs;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nessus.actions.jaxrs.main.JaxrsConfig;
import io.nessus.actions.jaxrs.service.JaxrsService;
import io.nessus.actions.jaxrs.service.KeycloakService;
import io.nessus.actions.jaxrs.service.UserModelsService;
import io.nessus.common.service.Service;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
	info = @Info(title = "Fuse TryIt - Jaxrs API", version = "1.0.0"), 
	externalDocs = @ExternalDocumentation(url = "https://github.com/tdiesler/nessus-actions"))
@ApplicationPath("/jaxrs/api")
public class JaxrsApplication extends Application {
	
	static final Logger LOG = LoggerFactory.getLogger(JaxrsApplication.class);
	
	private static JaxrsApplication INSTANCE;
	
	private JaxrsConfig config;
	
	public JaxrsApplication() {
		this(JaxrsConfig.createConfig());
	}
	
	public JaxrsApplication(JaxrsConfig config) {
		this.config = config;
		config.addService(new KeycloakService(config));
		config.addService(new JaxrsService(config));
		config.addService(new UserModelsService(config));
		INSTANCE = this;
	}
	
	public static JaxrsApplication getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new JaxrsApplication();
		}
		return INSTANCE;
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

	public JaxrsConfig getConfig() {
		return config;
	}

	public <T extends Service> T getService(Class<T> type) {
		return config.getService(type);
	}
}
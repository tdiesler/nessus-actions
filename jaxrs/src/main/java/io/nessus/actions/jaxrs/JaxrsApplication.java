package io.nessus.actions.jaxrs;

import java.sql.Connection;
import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.nessus.actions.core.NessusConfig;
import io.nessus.actions.core.jaxrs.AbstractApplication;
import io.nessus.h2.ConnectionFactory;
import io.nessus.h2.DBUtils;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
	info = @Info(title = "Fuse TryIt - Jaxrs API", version = "1.0.0"), 
	externalDocs = @ExternalDocumentation(url = "https://github.com/tdiesler/nessus-actions"))
public class JaxrsApplication extends AbstractApplication {
	
	public JaxrsApplication(NessusConfig config) throws Exception {
		super(config);
		
		ConnectionFactory factory = new ConnectionFactory(config);
		try (Connection con = factory.createConnection(Duration.ofSeconds(20))) {
			DBUtils.createDatabase(con, "createdb.sql");
		}
	}
	
	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<>();
		classes.add(ModelResource.class);
		classes.add(ModelListResource.class);
		classes.add(UserListResource.class);
		classes.add(UserResource.class);
		return Collections.unmodifiableSet(classes);
	}
}
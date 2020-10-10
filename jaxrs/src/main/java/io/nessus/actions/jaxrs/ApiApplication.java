package io.nessus.actions.jaxrs;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nessus.actions.jaxrs.service.ApiService;
import io.nessus.actions.jaxrs.service.SessionManagerService;
import io.nessus.common.service.Service;

@ApplicationPath("/jaxrs/api")
public class ApiApplication extends Application {
	
	static final Logger LOG = LoggerFactory.getLogger(ApiApplication.class);
	
	private static ApiApplication INSTANCE;
	
	private JaxrsConfig config;
	
	public ApiApplication() {
		this(JaxrsConfig.createConfig());
	}
	
	public ApiApplication(JaxrsConfig config) {
		this.config = config;
		config.addService(new ApiService(config));
		config.addService(new SessionManagerService(config));
		INSTANCE = this;
	}
	
	public static ApiApplication getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ApiApplication();
		}
		return INSTANCE;
	}
	
	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<>();
		classes.add(ApiUserDelete.class);
		classes.add(ApiUserRegister.class);
		classes.add(ApiUserStatus.class);
		classes.add(ApiUserToken.class);
		return Collections.unmodifiableSet(classes);
	}

	public JaxrsConfig getConfig() {
		return config;
	}

	public ApiService getApiService() {
		return config.getService(ApiService.class);
	}
	
	public <T extends Service> T getService(Class<T> type) {
		return config.getService(type);
	}
}
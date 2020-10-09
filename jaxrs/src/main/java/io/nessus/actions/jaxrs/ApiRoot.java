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

@ApplicationPath("/tryit/api")
public class ApiRoot extends Application {
	
	static final Logger LOG = LoggerFactory.getLogger(ApiRoot.class);
	
	private static ApiRoot INSTANCE;
	
	private ApiConfig config;
	
	public ApiRoot() {
		this(ApiConfig.createConfig());
	}
	
	public ApiRoot(ApiConfig config) {
		this.config = config;
		config.addService(new ApiService(config));
		config.addService(new SessionManagerService(config));
		INSTANCE = this;
	}
	
	public static ApiRoot getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ApiRoot();
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

	public ApiConfig getConfig() {
		return config;
	}

	public ApiService getApiService() {
		return config.getService(ApiService.class);
	}
	
	public <T extends Service> T getService(Class<T> type) {
		return config.getService(type);
	}
}
package io.nessus.actions.portal.resources;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nessus.actions.portal.PortalConfig;
import io.nessus.common.service.Service;

@ApplicationPath("/portal")
public class PortalApi extends Application {
	
	static final Logger LOG = LoggerFactory.getLogger(PortalApi.class);
	
	private static PortalApi INSTANCE;
	
	private PortalConfig config;
	
	public PortalApi() {
		config = PortalConfig.createConfig();
		config.addService(new ApiService(config));
		INSTANCE = this;
	}
	
	public PortalApi(PortalConfig config) {
		this.config = config;
		INSTANCE = this;
	}
	
	public static PortalApi getInstance() {
		return INSTANCE;
	}
	
	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<>();
		classes.add(PortalStatus.class);
		classes.add(UserRegister.class);
		return Collections.unmodifiableSet(classes);
	}

	public PortalConfig getConfig() {
		return config;
	}

	public ApiService getApiService() {
		return getConfig().getService(ApiService.class);
	}
	
	public <T extends Service> T getService(Class<T> type) {
		return getConfig().getService(type);
	}
}
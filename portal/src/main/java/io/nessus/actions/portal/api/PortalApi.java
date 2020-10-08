package io.nessus.actions.portal.api;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nessus.actions.portal.PortalConfig;
import io.nessus.actions.portal.service.ApiService;
import io.nessus.actions.portal.service.SessionManagerService;
import io.nessus.common.service.Service;

@ApplicationPath("/portal/api")
public class PortalApi extends Application {
	
	static final Logger LOG = LoggerFactory.getLogger(PortalApi.class);
	
	private static PortalApi INSTANCE;
	
	private PortalConfig config;
	
	public PortalApi() {
		this(PortalConfig.createConfig());
	}
	
	public PortalApi(PortalConfig config) {
		this.config = config;
		config.addService(new ApiService(config));
		config.addService(new SessionManagerService(config));
		INSTANCE = this;
	}
	
	public static PortalApi getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new PortalApi();
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

	public PortalConfig getConfig() {
		return config;
	}

	public ApiService getApiService() {
		return config.getService(ApiService.class);
	}
	
	public <T extends Service> T getService(Class<T> type) {
		return config.getService(type);
	}
}
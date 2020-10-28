package io.nessus.actions.core.jaxrs;

import javax.ws.rs.core.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nessus.actions.core.NessusConfig;
import io.nessus.actions.core.service.KeycloakService;
import io.nessus.common.service.Service;

public abstract class AbstractApplication extends Application {
	
	protected final Logger LOG = LoggerFactory.getLogger(getClass());
	
	private NessusConfig config;
	
	public AbstractApplication(NessusConfig config) {
		this.config = config;
		config.addService(new KeycloakService(config));
	}
	
	public NessusConfig getConfig() {
		return config;
	}

	public <T extends Service> T getService(Class<T> type) {
		return config.getService(type);
	}
}
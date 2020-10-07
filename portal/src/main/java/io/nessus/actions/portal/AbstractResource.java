package io.nessus.actions.portal;

import io.nessus.common.ConfigSupport;

public abstract class AbstractResource extends ConfigSupport<PortalConfig> {
	
	protected final PortalApi api;
	
	protected AbstractResource() {
		super(PortalApi.getInstance().getConfig());
		this.api = PortalApi.getInstance();
	}
}
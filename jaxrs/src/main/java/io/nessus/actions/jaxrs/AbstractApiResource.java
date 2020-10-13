package io.nessus.actions.jaxrs;

import io.nessus.actions.jaxrs.main.JaxrsConfig;

public abstract class AbstractApiResource extends AbstractResource<JaxrsConfig> {
	
	protected AbstractApiResource() {
		super(JaxrsApplication.getInstance().getConfig());
	}
}
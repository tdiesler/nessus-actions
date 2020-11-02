package io.nessus.actions.jaxrs.service;

import io.nessus.actions.core.NessusConfig;

abstract class AbstractMavenBuilderService extends AbstractJaxrsService implements MavenBuilderService {

	public AbstractMavenBuilderService(NessusConfig config) {
		super(config);
	}

	@Override
	public String getType() {
		return MavenBuilderService.class.getName();
	}
}
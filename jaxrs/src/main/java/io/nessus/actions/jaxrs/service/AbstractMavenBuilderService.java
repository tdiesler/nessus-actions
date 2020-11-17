package io.nessus.actions.jaxrs.service;

import io.nessus.actions.core.NessusConfig;
import io.nessus.common.service.Service;

abstract class AbstractMavenBuilderService extends AbstractJaxrsService implements MavenBuilderService {

	public AbstractMavenBuilderService(NessusConfig config) {
		super(config);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Service> Class<T> getType() {
		return (Class<T>) MavenBuilderService.class;
	}
}
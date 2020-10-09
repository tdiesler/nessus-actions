package io.nessus.actions.jaxrs.service;

import io.nessus.common.BasicConfig;
import io.nessus.common.service.AbstractBasicService;

public class AbstractService<T extends BasicConfig> extends AbstractBasicService<T> {

	public AbstractService(T config) {
		super(config);
	}
}
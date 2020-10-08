package io.nessus.actions.portal.service;

import io.nessus.actions.portal.PortalConfig;
import io.nessus.common.service.AbstractBasicService;

public class AbstractService extends AbstractBasicService<PortalConfig> {

	public AbstractService(PortalConfig config) {
		super(config);
	}
}
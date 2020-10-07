package io.nessus.actions.portal.api;

import io.nessus.actions.portal.PortalConfig;
import io.nessus.common.service.AbstractBasicService;

public class AbstractPortalService extends AbstractBasicService<PortalConfig> {

	public AbstractPortalService(PortalConfig config) {
		super(config);
	}
}
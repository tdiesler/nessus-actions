package io.nessus.actions.portal.api;

import io.nessus.actions.portal.AbstractResource;
import io.nessus.actions.portal.PortalApi;

abstract class AbstractApiResource extends AbstractResource {
	
	AbstractApiResource(PortalApi app) {
		super(app);
	}
}
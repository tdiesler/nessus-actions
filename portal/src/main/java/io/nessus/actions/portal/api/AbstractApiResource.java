package io.nessus.actions.portal.api;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import io.nessus.actions.portal.AbstractResource;

abstract class AbstractApiResource extends AbstractResource {
	
	@Context
	protected HttpServletRequest httpRequest;
	
}
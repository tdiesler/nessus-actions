package io.nessus.actions.portal.web;

import org.apache.velocity.VelocityContext;

import io.undertow.server.HttpServerExchange;

public class WebHome extends AbstractWebResource  {
	
    @Override
	protected String handlePageRequest(HttpServerExchange exchange, VelocityContext context) throws Exception {

		WebUserStatus userStatus = new WebUserStatus();
		return userStatus.handlePageRequest(exchange, context);
	}
}
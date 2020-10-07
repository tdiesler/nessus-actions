package io.nessus.actions.portal.web;

import org.apache.velocity.VelocityContext;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;

public class WebUserLogout extends AbstractWebResource  {
	
    @Override
	protected String handlePageRequest(HttpServerExchange exchange, VelocityContext context) throws Exception {
    	
    	Session session = getSession(exchange, false);
    	if (session != null) {
    		session.invalidate(exchange);
    	}
        
    	redirectToLogin(exchange);
    	
        return null;
	}
}
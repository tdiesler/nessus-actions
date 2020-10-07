package io.nessus.actions.portal.web;

import org.apache.velocity.VelocityContext;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;

public class WebUserStatus extends AbstractWebResource  {
	
    @Override
	protected String handlePageRequest(HttpServerExchange exchange, VelocityContext context) throws Exception {

    	Session session = getSession(exchange, false);

    	UserSession status = getAttribute(session, UserSession.class);
    	if (status != null) {
    		
        	context.put("status", status);
            return "template/user-status.vm";
            
    	} else {
    		
    		redirectToLogin(exchange);
    		return null;
    	}
	}
}
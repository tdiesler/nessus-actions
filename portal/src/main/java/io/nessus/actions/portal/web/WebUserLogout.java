package io.nessus.actions.portal.web;

import org.apache.velocity.VelocityContext;

import io.nessus.actions.jaxrs.type.UserInfo;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;

public class WebUserLogout extends AbstractWebResource  {
	
	@Override
	protected void handleActionRequest(HttpServerExchange exchange, VelocityContext context) throws Exception {
		
    	Session session = getSession(exchange, false);
    	if (session != null) {
        	UserInfo status = getAttribute(session, UserInfo.class);
        	logInfo("Logout: {}", status != null ? status.getUsername() : null);
    		session.invalidate(exchange);
    	}
    	
    	redirectToLogin(exchange);
	}
}
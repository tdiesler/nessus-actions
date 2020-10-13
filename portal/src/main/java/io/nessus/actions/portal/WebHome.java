package io.nessus.actions.portal;

import org.apache.velocity.VelocityContext;

import io.nessus.actions.portal.main.PortalConfig;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;

public class WebHome extends AbstractWebResource {

	public WebHome(PortalConfig config) {
		super(config);
	}

	@Override
	protected String handlePageRequest(HttpServerExchange exchange, VelocityContext context) throws Exception {
		
		Session session = getSession(exchange, false);
		if (session != null) {
			
	    	redirectTo(exchange, "/user/home");
	    	
		} else {
			
	    	redirectToLogin(exchange);
		}
		
		return null;
	}
}
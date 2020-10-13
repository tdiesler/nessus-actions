package io.nessus.actions.portal;

import org.apache.velocity.VelocityContext;

import io.nessus.actions.jaxrs.type.UserState;
import io.nessus.actions.portal.main.PortalConfig;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;

public class WebUserLogout extends AbstractUserResource  {
	
	public WebUserLogout(PortalConfig config) {
		super(config);
	}

	@Override
	protected void handleActionRequest(HttpServerExchange exchange, VelocityContext context, Session session) throws Exception {
		
    	UserState user = getAttribute(session, UserState.class);
    	logInfo("Logout: {}", user != null ? user.getUsername() : null);
		session.invalidate(exchange);
    	
    	redirectToLogin(exchange);
	}
}
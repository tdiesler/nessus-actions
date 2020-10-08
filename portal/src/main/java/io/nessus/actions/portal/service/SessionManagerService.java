package io.nessus.actions.portal.service;

import static io.undertow.server.session.SessionCookieConfig.DEFAULT_SESSION_ID;

import io.nessus.actions.portal.PortalConfig;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.session.InMemorySessionManager;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionCookieConfig;
import io.undertow.server.session.SessionManager;

public class SessionManagerService extends AbstractService {

	private final SessionManager sessionManager;
	
	public SessionManagerService(PortalConfig config) {
		super(config);
		sessionManager = new InMemorySessionManager("portal");
	}
	
	public Session createSession(HttpServerExchange exchange) {
		Session session = sessionManager.createSession(exchange, new SessionCookieConfig());    		
		return session;
	}

	public Session getSession(HttpServerExchange exchange) {
		Cookie cookie = exchange.getRequestCookies().get(DEFAULT_SESSION_ID);
		Session session = cookie != null ? sessionManager.getSession(cookie.getValue()) : null;
		return session;
	}

}
package io.nessus.actions.jaxrs.service;

import static io.undertow.server.session.SessionCookieConfig.DEFAULT_SESSION_ID;

import io.nessus.actions.jaxrs.JaxrsConfig;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.session.InMemorySessionManager;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionCookieConfig;
import io.undertow.server.session.SessionManager;

public class SessionManagerService extends AbstractService<JaxrsConfig> {

	private final SessionManager sessionManager;
	
	public SessionManagerService(JaxrsConfig config) {
		super(config);
		sessionManager = new InMemorySessionManager("portal");
	}
	
	public Session getSession(HttpServerExchange exchange, boolean create) {
		Cookie cookie = exchange.getRequestCookies().get(DEFAULT_SESSION_ID);
		Session session = cookie != null ? sessionManager.getSession(cookie.getValue()) : null;
		if (session == null && create) {
			session = sessionManager.createSession(exchange, new SessionCookieConfig());
		}
		return session;
	}

}
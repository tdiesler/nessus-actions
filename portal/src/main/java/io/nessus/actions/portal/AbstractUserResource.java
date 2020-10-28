package io.nessus.actions.portal;

import static io.nessus.actions.core.utils.ApiUtils.hasStatus;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.velocity.VelocityContext;

import io.nessus.actions.core.service.KeycloakService;
import io.nessus.actions.core.utils.ApiUtils;
import io.nessus.actions.jaxrs.type.UserState;
import io.nessus.actions.jaxrs.type.UserTokens;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;

abstract class AbstractUserResource extends AbstractWebResource {

	@Override
	protected final String handlePageRequest(HttpServerExchange exchange, VelocityContext context) throws Exception {
		
    	if (!validateUserSession(exchange, context)) {
			redirectToLogin(exchange);
			return null;
    	}
    	
		Session session = getSession(exchange, false);
		return handlePageRequest(exchange, context, session);
	}

	@Override
	protected final void handleActionRequest(HttpServerExchange exchange, VelocityContext context) throws Exception {
		
    	if (!validateUserSession(exchange, context)) {
			redirectToLogin(exchange);
			return;
    	}
    	
		Session session = getSession(exchange, false);
		handleActionRequest(exchange, context, session);
	}

	protected String handlePageRequest(HttpServerExchange exchange, VelocityContext context, Session session) throws Exception {
		throw new UnsupportedOperationException(exchange.getRequestPath());
	}

	protected void handleActionRequest(HttpServerExchange exchange, VelocityContext context, Session session) throws Exception {
		throw new UnsupportedOperationException(exchange.getRequestPath());
	}

	private boolean validateUserSession(HttpServerExchange exchange, VelocityContext context) throws Exception {
		
		Session session = getSession(exchange, false);
		UserTokens tokens = getAttribute(session, UserTokens.class);
		if (tokens == null)
			return false;
		
		// Refresh the access token
		
		KeycloakService keycloak = getKeycloakService();
		String accessToken = keycloak.refreshAccessToken(tokens.refreshToken);
		String userId = tokens.userId;
		
		setAttribute(session, new UserTokens(tokens, accessToken));
		
		// Update the user status
		
		String url = ApiUtils.jaxrsUrl(config, "/api/user/" + userId + "/state");
		Response res = withClient(url, target -> target.request()
					.header("Authorization", "Bearer " + accessToken)
					.get());
		
		if (!hasStatus(res, Status.OK))
			return false;
		
		UserState userStatus = res.readEntity(UserState.class);
		setAttribute(session, userStatus);
    	
		return true;
	}
}
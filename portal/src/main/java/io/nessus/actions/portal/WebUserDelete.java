package io.nessus.actions.portal;

import java.net.URI;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.velocity.VelocityContext;

import io.nessus.actions.core.utils.ApiUtils;
import io.nessus.actions.jaxrs.type.UserTokens;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;

public class WebUserDelete extends AbstractUserResource  {
	
	@Override
	protected void handleActionRequest(HttpServerExchange exchange, VelocityContext context, Session session) throws Exception {
		
		UserTokens tokens = getAttribute(session, UserTokens.class);
		String accessToken = tokens.accessToken;
		String userId = tokens.userId;
		
		URI uri = ApiUtils.jaxrsUri(config, "/api/user/" + userId);
		Response res = withClient(uri, target -> target.request()
					.header("Authorization", "Bearer " + accessToken)
					.delete());
		
		assertStatus(res, Status.NO_CONTENT);
    	
    	redirectToLogin(exchange);
	}
}
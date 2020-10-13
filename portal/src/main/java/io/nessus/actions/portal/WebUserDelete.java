package io.nessus.actions.portal;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.velocity.VelocityContext;

import io.nessus.actions.jaxrs.type.UserTokens;
import io.nessus.actions.portal.main.PortalConfig;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;

public class WebUserDelete extends AbstractUserResource  {
	
	public WebUserDelete(PortalConfig config) {
		super(config);
	}

	@Override
	protected void handleActionRequest(HttpServerExchange exchange, VelocityContext context, Session session) throws Exception {
		
		UserTokens tokens = getAttribute(session, UserTokens.class);
		String accessToken = tokens.accessToken;
		String userId = tokens.userId;
		
		String url = jaxrsUrl("/api/user/" + userId);
		Response res = withClient(url, target -> target.request()
					.header("Authorization", "Bearer " + accessToken)
					.delete());
		
		assertStatus(res, Status.NO_CONTENT);
    	
    	redirectToLogin(exchange);
	}
}
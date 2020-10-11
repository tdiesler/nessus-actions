package io.nessus.actions.portal.web;

import static io.nessus.actions.jaxrs.utils.JaxrsUtils.jaxrsUrl;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.velocity.VelocityContext;

import io.nessus.actions.jaxrs.service.KeycloakService;
import io.nessus.actions.jaxrs.type.KeycloakTokens;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;

public class WebUserDelete extends AbstractWebResource  {
	
	@Override
	protected void handleActionRequest(HttpServerExchange exchange, VelocityContext context) throws Exception {
		
    	Session session = getSession(exchange, false);
		KeycloakTokens tokens = getAttribute(session, KeycloakTokens.class);
		
    	if (tokens != null) {
    		
    		KeycloakService apisrv = api.getApiService();
    		String accessToken = apisrv.refreshAccessToken(tokens.refreshToken);
    		
    		Response res = withClient(jaxrsUrl("/api/user"), 
    				target -> target.request()
    					.header("Authorization", "Bearer " + accessToken)
    					.delete());
    		
    		assertStatus(res, Status.NO_CONTENT);
    	}
    	
    	redirectToLogin(exchange);
	}
}
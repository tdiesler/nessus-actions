package io.nessus.actions.portal.web;

import static io.nessus.actions.portal.api.ApiUtils.portalUrl;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.velocity.VelocityContext;

import io.nessus.actions.portal.api.type.KeycloakTokens;
import io.nessus.actions.portal.service.ApiService;
import io.undertow.server.HttpServerExchange;

public class WebUserDelete extends AbstractWebResource  {
	
	@Override
	protected void handleActionRequest(HttpServerExchange exchange, VelocityContext context) throws Exception {
		
    	KeycloakTokens tokens = getAttribute(getSession(exchange), KeycloakTokens.class);
    	if (tokens != null) {
    		
    		ApiService apisrv = api.getApiService();
    		String accessToken = apisrv.refreshAccessToken(tokens.refreshToken);
    		
    		Response res = withClient(portalUrl("/api/user"), 
    				target -> target.request()
    					.header("Authorization", "Bearer " + accessToken)
    					.delete());
    		
    		assertStatus(res, Status.NO_CONTENT);
    	}
    	
    	redirectToLogin(exchange);
	}
}
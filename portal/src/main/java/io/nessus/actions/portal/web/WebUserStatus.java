package io.nessus.actions.portal.web;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.velocity.VelocityContext;

import io.nessus.actions.jaxrs.service.KeycloakService;
import io.nessus.actions.jaxrs.type.KeycloakTokens;
import io.nessus.actions.jaxrs.type.UserInfo;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;

public class WebUserStatus extends AbstractWebResource  {
	
    @Override
	protected String handlePageRequest(HttpServerExchange exchange, VelocityContext context) throws Exception {

    	Session session = getSession(exchange, false);
		KeycloakTokens tokens = getAttribute(session, KeycloakTokens.class);
		
    	if (tokens != null) {
    		
    		KeycloakService apisrv = api.getApiService();
    		String accessToken = apisrv.refreshAccessToken(tokens.refreshToken);
    		
    		Response res = withClient(jaxrsUrl("/api/user/status"), 
    				target -> target.request()
    					.header("Authorization", "Bearer " + accessToken)
    					.get());
    		
    		assertStatus(res, Status.OK);
    		
    		UserInfo userInfo = res.readEntity(UserInfo.class);
        	context.put("user", userInfo);
        	
            return "template/user-status.vm";
            
    	} else {
    		
    		WebUserLogin userLogin = new WebUserLogin();
			return userLogin.handlePageRequest(exchange, context);
    	}
	}
}
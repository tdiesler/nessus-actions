package io.nessus.actions.portal.web;

import static io.nessus.actions.portal.api.ApiUtils.portalUrl;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.velocity.VelocityContext;

import io.nessus.actions.portal.api.type.KeycloakTokens;
import io.nessus.actions.portal.api.type.UserInfo;
import io.nessus.actions.portal.service.ApiService;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;

public class WebUserStatus extends AbstractWebResource  {
	
    @Override
	protected String handlePageRequest(HttpServerExchange exchange, VelocityContext context) throws Exception {

    	Session session = getSession(exchange, false);
		KeycloakTokens tokens = getAttribute(session, KeycloakTokens.class);
		
    	if (tokens != null) {
    		
    		ApiService apisrv = api.getApiService();
    		String accessToken = apisrv.refreshAccessToken(tokens.refreshToken);
    		
    		Response res = withClient(portalUrl("/api/user/status"), 
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
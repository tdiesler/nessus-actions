package io.nessus.actions.portal.web;

import static io.nessus.actions.jaxrs.ApiUtils.portalUrl;

import java.net.URL;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.velocity.VelocityContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.nessus.actions.jaxrs.service.ApiService;
import io.nessus.actions.jaxrs.type.KeycloakTokens;
import io.nessus.actions.jaxrs.type.KeycloakUserInfo;
import io.nessus.actions.jaxrs.type.User;
import io.nessus.actions.jaxrs.type.UserInfo;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;

public class WebUserLogin extends AbstractWebResource  {
	
    @Override
	protected String handlePageRequest(HttpServerExchange exchange, VelocityContext context) throws Exception {
    	
		URL resUrl = getClass().getResource("/json/user-register.json");
		
		ObjectMapper mapper = new ObjectMapper();
		User user = mapper.readValue(resUrl, User.class);
        context.put("user", user);
		
        return "template/user-login.vm";
	}

	@Override
	protected void handleActionRequest(HttpServerExchange exchange, VelocityContext context) throws Exception {

		// Get the user tokens
		
		MultivaluedMap<String, String> qparams = getRequestParameters(exchange);
		
		Response res = withClient(portalUrl("/api/user/token"), 
				target -> target.request(MediaType.APPLICATION_FORM_URLENCODED)
				.post(Entity.form(qparams)));
		
		assertStatus(res, Status.OK);
		
		KeycloakTokens tokens = res.readEntity(KeycloakTokens.class);
		String accessToken = tokens.accessToken;
		
		// Get the user info using the access token 
		
		ApiService apisrv = api.getApiService();
		res = apisrv.getUserInfo(accessToken);
		
		assertStatus(res, Status.OK);

		// Store both, then tokens and the user info in the session
		
		KeycloakUserInfo kcinfo = res.readEntity(KeycloakUserInfo.class);
    	UserInfo userinfo = new UserInfo(kcinfo);
    	
    	Session session = getSession(exchange, true);
		setAttribute(session, tokens);
		setAttribute(session, userinfo);
		
    	context.put("user", userinfo);
        
    	redirectTo(exchange, "/status");
	}
}
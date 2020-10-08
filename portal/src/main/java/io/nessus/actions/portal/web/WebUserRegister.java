package io.nessus.actions.portal.web;

import static io.nessus.actions.portal.api.ApiUtils.portalUrl;

import java.net.URL;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.velocity.VelocityContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.nessus.actions.portal.api.type.KeycloakTokens;
import io.nessus.actions.portal.api.type.KeycloakUserInfo;
import io.nessus.actions.portal.api.type.User;
import io.nessus.actions.portal.api.type.UserInfo;
import io.nessus.actions.portal.service.ApiService;
import io.nessus.common.AssertArg;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;

public class WebUserRegister extends AbstractWebResource  {
	
    @Override
	protected String handlePageRequest(HttpServerExchange exchange, VelocityContext context) throws Exception {
    	
		URL resUrl = getClass().getResource("/json/user-register.json");
		
		ObjectMapper mapper = new ObjectMapper();
		User user = mapper.readValue(resUrl, User.class);
        context.put("user", user);
		
        return "template/user-register.vm";
	}

	@Override
	protected void handleActionRequest(HttpServerExchange exchange, VelocityContext context) throws Exception {

		MultivaluedMap<String, String> qparams = getRequestParameters(exchange);
		
        String firstName = qparams.getFirst("firstName");
        String lastName = qparams.getFirst("lastName");
        String email = qparams.getFirst("email");
        String username = qparams.getFirst("username");
        String password = qparams.getFirst("password");
        String retype = qparams.getFirst("retype");
        
        User user = new User(firstName, lastName, email, username, password);
        AssertArg.isEqual(password, retype, "Password does not match");
        
		Response res = withClient(portalUrl("/api/users"), 
				target -> target.request(MediaType.APPLICATION_JSON)
				.post(Entity.json(user)));
		
		assertStatus(res, Status.CREATED);
		
		KeycloakTokens tokens = res.readEntity(KeycloakTokens.class);
		String accessToken = tokens.accessToken;
		
		// Get the user info using the access token 
		
		ApiService apisrv = api.getApiService();
		res = apisrv.getUserInfo(accessToken);
		
		assertStatus(res, Status.OK);

		// Store both, then tokens and the user info in the session
		
		KeycloakUserInfo kcinfo = res.readEntity(KeycloakUserInfo.class);
    	UserInfo userinfo = new UserInfo(kcinfo);
    	
    	Session session = createSession(exchange);
		setAttribute(session, tokens);
		setAttribute(session, userinfo);
		
    	context.put("user", userinfo);
        
    	redirectTo(exchange, "/status");
    }
}
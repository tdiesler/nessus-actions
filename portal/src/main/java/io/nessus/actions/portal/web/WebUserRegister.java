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

import io.nessus.actions.portal.api.type.User;
import io.nessus.common.AssertArg;
import io.undertow.server.HttpServerExchange;

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
		
    	redirectToLogin(exchange);
    }
}
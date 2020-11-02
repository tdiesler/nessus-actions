package io.nessus.actions.portal;

import java.net.URI;
import java.net.URL;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.velocity.VelocityContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.nessus.actions.core.utils.ApiUtils;
import io.nessus.actions.jaxrs.type.UserRegister;
import io.nessus.actions.jaxrs.type.UserTokens;
import io.nessus.common.AssertArg;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;

public class WebUserLogin extends AbstractWebResource {

	@Override
	protected String handlePageRequest(HttpServerExchange exchange, VelocityContext context) throws Exception {
    	
		URL resUrl = getClass().getResource("/json/user-register.json");
		
		ObjectMapper mapper = new ObjectMapper();
		UserRegister user = mapper.readValue(resUrl, UserRegister.class);
        context.put("user", user);
		
        return "template/user-login.vm";
	}

	@Override
	protected void handleActionRequest(HttpServerExchange exchange, VelocityContext context) throws Exception {

		// Get the user tokens
		
		MultivaluedMap<String, String> reqprms = getRequestParameters(exchange);
		String type = reqprms.getFirst("type");
		
		if ("login".equals(type)) {
			
			doLogin(exchange, reqprms);
			
		} else if ("register".equals(type)) {
			
	        doRegister(exchange, reqprms);
	        
			doLogin(exchange, reqprms);
		}
	}

	private void doLogin(HttpServerExchange exchange, MultivaluedMap<String, String> qparams) throws Exception {
		
		URI uri = ApiUtils.jaxrsUri(config, "/api/users/login");
		Response res = withClient(uri, target -> target.request(MediaType.APPLICATION_FORM_URLENCODED)
				.post(Entity.form(qparams)));
		
		assertStatus(res, Status.OK);
		
		UserTokens tokens = res.readEntity(UserTokens.class);
		
		Session session = getSession(exchange, true);
		setAttribute(session, tokens);
		
		redirectHome(exchange);
	}

	private void doRegister(HttpServerExchange exchange, MultivaluedMap<String, String> qparams) {
		
		String firstName = qparams.getFirst("firstName");
		String lastName = qparams.getFirst("lastName");
		String email = qparams.getFirst("email");
		String username = qparams.getFirst("username");
		String password = qparams.getFirst("password");
		String retype = qparams.getFirst("retype");
		
		UserRegister user = new UserRegister(username, firstName, lastName, email, password);
		AssertArg.isEqual(password, retype, "Password does not match");
		
		URI uri = ApiUtils.jaxrsUri(config, "/api/users");
		Response res = withClient(uri, target -> target.request(MediaType.APPLICATION_JSON)
				.put(Entity.json(user)));
		
		assertStatus(res, Status.CREATED);
	}
}
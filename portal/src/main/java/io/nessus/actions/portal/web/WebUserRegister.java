package io.nessus.actions.portal.web;

import static io.nessus.actions.portal.api.ApiUtils.hasStatus;
import static io.nessus.actions.portal.api.ApiUtils.portalUrl;
import static io.nessus.actions.portal.api.ApiUtils.withClient;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.velocity.VelocityContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.nessus.actions.portal.api.User;
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
	protected String handleActionRequest(HttpServerExchange exchange, VelocityContext context) throws Exception {

		MultivaluedMap<String, String> qparams = getRequestParameters(exchange);
		
        String firstName = qparams.getFirst("firstName");
        String lastName = qparams.getFirst("lastName");
        String email = qparams.getFirst("email");
        String username = qparams.getFirst("username");
        String password = qparams.getFirst("password");
        String retype = qparams.getFirst("retype");
        
        AssertArg.isEqual(password, retype, "Password does not match");
        
		Response res = withClient(client -> {
	        User user = new User(firstName, lastName, email, username, password);
			return client.target(portalUrl("/api/users"))
					.request(MediaType.APPLICATION_JSON)
					.post(Entity.json(user));
		});
		
		if (!hasStatus(res, Status.CREATED)) {
			int status = res.getStatus();
			String reason = res.getStatusInfo().getReasonPhrase();
			String errmsg = String.format("%s %s", status, reason);
	        return errorPage(context, null, errmsg);
		}
		
		@SuppressWarnings("unchecked")
		Map<String, String> resmap = res.readEntity(LinkedHashMap.class);
		String refreshToken = resmap.get("refresh_token");
		
    	UserSession userSession = new UserSession(username, refreshToken);
    	UserStatus userStatus = new UserStatus(username);
    	
    	Session session = getSession(exchange, true);
		setAttribute(session, userSession);
		setAttribute(session, userStatus);
    	context.put("status", userStatus);
        
        return "template/user-status.vm";
    }

}
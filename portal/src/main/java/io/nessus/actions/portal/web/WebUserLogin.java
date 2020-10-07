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
	protected String handleActionRequest(HttpServerExchange exchange, VelocityContext context) throws Exception {

		MultivaluedMap<String, String> qparams = getRequestParameters(exchange);
		
		Response res = withClient(client -> {
			return client.target(portalUrl("/api/login"))
				.request(MediaType.APPLICATION_FORM_URLENCODED)
				.post(Entity.form(qparams));
		});
		
		if (!hasStatus(res, Status.OK)) {
			int status = res.getStatus();
			String reason = res.getStatusInfo().getReasonPhrase();
			String errmsg = String.format("%s %s", status, reason);
	        return errorPage(context, null, errmsg);
		}
		
		@SuppressWarnings("unchecked")
		Map<String, String> resmap = res.readEntity(LinkedHashMap.class);
		String refreshToken = resmap.get("refresh_token");
		String username = resmap.get("username");
		
    	UserSession userSession = new UserSession(username, refreshToken);
    	UserStatus userStatus = new UserStatus(username);
    	
    	Session session = getSession(exchange, true);
		setAttribute(session, userSession);
		setAttribute(session, userStatus);
    	context.put("status", userStatus);
        
    	redirectTo(exchange, "/status");
    	
        return null;
	}
}
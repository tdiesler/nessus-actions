package io.nessus.actions.portal;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.velocity.VelocityContext;

import io.nessus.actions.jaxrs.type.UserModels;
import io.nessus.actions.jaxrs.type.UserTokens;
import io.nessus.actions.portal.main.PortalConfig;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;

public class WebUserModels extends AbstractUserResource {

	public WebUserModels(PortalConfig config) {
		super(config);
	}

	@Override
	protected String handlePageRequest(HttpServerExchange exchange, VelocityContext context, Session session) throws Exception {
		
		UserTokens tokens = getAttribute(session, UserTokens.class);
		String accessToken = tokens.accessToken;
		String userId = tokens.userId;
		
		String url = jaxrsUrl("/api/user/" + userId + "/models");
		Response res = withClient(url, target -> target
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)
				.get());
	
		assertStatus(res, Status.OK);
	
		UserModels userModels = res.readEntity(UserModels.class);
		context.put("models", userModels);
		
        return "template/user-models.vm";
	}
}
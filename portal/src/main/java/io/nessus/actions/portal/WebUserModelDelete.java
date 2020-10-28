package io.nessus.actions.portal;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.velocity.VelocityContext;

import io.nessus.actions.core.utils.ApiUtils;
import io.nessus.actions.jaxrs.type.UserTokens;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;

public class WebUserModelDelete extends AbstractUserResource {

	@Override
	protected void handleActionRequest(HttpServerExchange exchange, VelocityContext context, Session session) throws Exception {
		
		UserTokens tokens = getAttribute(session, UserTokens.class);
		String accessToken = tokens.accessToken;
		String userId = tokens.userId;
		
		String modelId = getRequestParameters(exchange).getFirst("modelId");
		
		// Delete Model
		
		// DELETE http://localhost:7080/jaxrs/api/user/{userId}/model/{modelId}
		//
		
		String url = ApiUtils.jaxrsUrl(config, "/api/user/" + userId + "/model/" + modelId);
		Response res = withClient(url, target -> target.request()
				.header("Authorization", "Bearer " + accessToken)
				.delete());
	
		assertStatus(res, Status.NO_CONTENT);
		
		redirectTo(exchange, "/user/models");
	}

}
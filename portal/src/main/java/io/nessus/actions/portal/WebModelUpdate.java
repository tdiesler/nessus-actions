package io.nessus.actions.portal;

import java.net.URI;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.velocity.VelocityContext;

import io.nessus.actions.core.utils.ApiUtils;
import io.nessus.actions.jaxrs.type.UserModel;
import io.nessus.actions.jaxrs.type.UserTokens;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;

public class WebModelUpdate extends AbstractUserResource {

	@Override
	protected String handlePageRequest(HttpServerExchange exchange, VelocityContext context, Session session) throws Exception {

		UserTokens tokens = getAttribute(session, UserTokens.class);
		String accessToken = tokens.accessToken;
		String userId = tokens.userId;
		
		String modelId = getRequestParameters(exchange).getFirst("modelId");
		
		// Get Model
		
		// GET http://localhost:8200/jaxrs/api/user/{userId}/model/{modelId}
		// 
		// {
		//	  "userId": "myuser",
		//	  "content": "some model content", 
		// }

		URI uri = ApiUtils.jaxrsUri(config, "/api/user/" + userId + "/model/" + modelId);
		Response res = withClient(uri, target -> target.request()
				.header("Authorization", "Bearer " + accessToken)
				.get());
	
		assertStatus(res, Status.OK);
		
		UserModel userModel = res.readEntity(UserModel.class);
		
		context.put("model", userModel);
		
		return "template/model-update.vm";
	}

	@Override
	protected void handleActionRequest(HttpServerExchange exchange, VelocityContext context, Session session) throws Exception {
		
		UserTokens tokens = getAttribute(session, UserTokens.class);
		String accessToken = tokens.accessToken;
		String userId = tokens.userId;
		
		// Update Model
		
		// POST http://localhost:8200/jaxrs/api/user/{userId}/model/{modelId}
		//
		
		MultivaluedMap<String, String> reqprms = getRequestParameters(exchange);
		String modelId = reqprms.getFirst("modelId");
		String content = reqprms.getFirst("content");
		
		UserModel modelUpd = new UserModel(modelId, userId, content);

		URI uri = ApiUtils.jaxrsUri(config, "/api/user/" + userId + "/model/" + modelId);
		Response res = withClient(uri, target -> target.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)
				.post(Entity.json(modelUpd)));
	
		assertStatus(res, Status.OK);
		
		redirectTo(exchange, "/user/models");
	}
}
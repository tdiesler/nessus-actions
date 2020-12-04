package io.nessus.actions.portal;

import java.io.InputStream;
import java.net.URI;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.velocity.VelocityContext;

import io.nessus.actions.core.model.RouteModel;
import io.nessus.actions.core.utils.ApiUtils;
import io.nessus.actions.jaxrs.type.ModelAdd;
import io.nessus.actions.jaxrs.type.UserTokens;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;

public class WebModelCreate extends AbstractUserResource {

	@Override
	protected String handlePageRequest(HttpServerExchange exchange, VelocityContext context, Session session) throws Exception {
		
		UserTokens tokens = getAttribute(session, UserTokens.class);
		String userId = tokens.userId;
		
		InputStream input = getClass().getResourceAsStream("/model/crypto-ticker.yaml");
		RouteModel model = RouteModel.read(input);

		String content = model.toString();
		ModelAdd modelAdd = new ModelAdd(userId, content);
		
		context.put("model", modelAdd);
		
		return "template/model-create.vm";
	}

	@Override
	protected void handleActionRequest(HttpServerExchange exchange, VelocityContext context, Session session) throws Exception {
		
		UserTokens tokens = getAttribute(session, UserTokens.class);
		String accessToken = tokens.accessToken;
		String userId = tokens.userId;
		
		// Create Model
		
		// PUT http://localhost:8200/jaxrs/api/user/{userId}/models
		// 
		// {
		//	  "userId": "myuser",
		//	  "content": "some model content", 
		// }

		MultivaluedMap<String, String> reqprms = getRequestParameters(exchange);
		String content = reqprms.getFirst("content");
		
		ModelAdd modelAdd = new ModelAdd(userId, content);
		
		URI uri = ApiUtils.jaxrsUri(config, "/api/user/" + userId + "/models");
		Response res = withClient(uri, target -> target
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + accessToken)
					.put(Entity.json(modelAdd)));
		
		assertStatus(res, Status.CREATED);
		
		redirectTo(exchange, "/user/models");
	}
}
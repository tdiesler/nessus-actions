package io.nessus.actions.portal;

import java.io.InputStream;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.velocity.VelocityContext;

import io.nessus.actions.jaxrs.type.UserModelAdd;
import io.nessus.actions.jaxrs.type.UserTokens;
import io.nessus.actions.model.Model;
import io.nessus.actions.portal.main.PortalConfig;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;

public class WebUserModelCreate extends AbstractUserResource {

	public WebUserModelCreate(PortalConfig config) {
		super(config);
	}

	@Override
	protected String handlePageRequest(HttpServerExchange exchange, VelocityContext context, Session session) throws Exception {
		
		UserTokens tokens = getAttribute(session, UserTokens.class);
		String userId = tokens.userId;
		
		InputStream input = getClass().getResourceAsStream("/model/crypto-ticker.yaml");
		Model model = Model.read(input);

		String title = model.getTitle();
		String content = model.toString();
		UserModelAdd modelAdd = new UserModelAdd(userId, title, content);
		
		context.put("model", modelAdd);
		
		return "template/user-model-create.vm";
	}

	@Override
	protected void handleActionRequest(HttpServerExchange exchange, VelocityContext context, Session session) throws Exception {
		
		UserTokens tokens = getAttribute(session, UserTokens.class);
		String accessToken = tokens.accessToken;
		String userId = tokens.userId;
		
		// Create Model
		
		// PUT http://localhost:7080/jaxrs/api/user/{userId}/models
		// 
		// {
		//	  "userId": "myuser",
		//	  "content": "some model content", 
		// }

		MultivaluedMap<String, String> reqprms = getRequestParameters(exchange);
		String title = reqprms.getFirst("title");
		String content = reqprms.getFirst("content");
		
		UserModelAdd modelAdd = new UserModelAdd(userId, title, content);
		
		String url = jaxrsUrl("/api/user/" + userId + "/models");
		Response res = withClient(url, target -> target
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + accessToken)
					.put(Entity.json(modelAdd)));
		
		assertStatus(res, Status.CREATED);
		
		redirectTo(exchange, "/user/models");
	}
}
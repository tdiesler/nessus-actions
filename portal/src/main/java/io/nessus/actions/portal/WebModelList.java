package io.nessus.actions.portal;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.velocity.VelocityContext;

import io.nessus.actions.core.utils.ApiUtils;
import io.nessus.actions.jaxrs.type.ModelList;
import io.nessus.actions.jaxrs.type.UserTokens;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;

public class WebModelList extends AbstractUserResource {

	@Override
	protected String handlePageRequest(HttpServerExchange exchange, VelocityContext context, Session session) throws Exception {
		
		UserTokens tokens = getAttribute(session, UserTokens.class);
		String accessToken = tokens.accessToken;
		String userid = tokens.userId;
		
		// Get Models
		
		// GET http://localhost:8200/jaxrs/api/user/{userId}/models
		//
		
		URI uri = ApiUtils.jaxrsUri(config, "/api/user/" + userid + "/models");
		Response res = withClient(uri, target -> target
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)
				.get());
	
		assertStatus(res, Status.OK);
	
		ModelList models = res.readEntity(ModelList.class);
		context.put("models", models);

		models.getModels().forEach(m -> logInfo("{}", m));
		
        return "template/model-list.vm";
	}
}
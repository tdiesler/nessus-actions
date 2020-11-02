package io.nessus.actions.portal;

import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.velocity.VelocityContext;

import io.nessus.actions.core.types.MavenBuildHandle;
import io.nessus.actions.core.types.MavenBuildHandle.BuildStatus;
import io.nessus.actions.core.utils.ApiUtils;
import io.nessus.actions.jaxrs.type.UserModel;
import io.nessus.actions.jaxrs.type.UserModelList;
import io.nessus.actions.jaxrs.type.UserModelList.ModelRuntime;
import io.nessus.actions.jaxrs.type.UserTokens;
import io.nessus.common.AssertState;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.server.session.Session;

public class WebModelList extends AbstractUserResource {

	@Override
	protected String handlePageRequest(HttpServerExchange exchange, VelocityContext context, Session session) throws Exception {
		
		UserTokens tokens = getAttribute(session, UserTokens.class);
		String accessToken = tokens.accessToken;
		String userId = tokens.userId;
		
		// Get Models
		
		// GET http://localhost:8200/jaxrs/api/user/{userId}/models
		//
		
		URI uri = ApiUtils.jaxrsUri(config, "/api/user/" + userId + "/models");
		Response res = withClient(uri, target -> target
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)
				.get());
	
		assertStatus(res, Status.OK);
	
		UserModelList userModels = res.readEntity(UserModelList.class);
		Map<String, BuildStatus> buildStatusMap = new LinkedHashMap<>();
		context.put("buildStatusMap", buildStatusMap);
		context.put("userModels", userModels);
		
		for (UserModel userModel : userModels.getModels()) {

			String modelId = userModel.getModelId();
			String projId = modelId + "/" + ModelRuntime.standalone;
			
			// Get Build Status
			
			// GET http://localhost:8100/maven/api/build/{projId}/status
			//
			
			uri = ApiUtils.jaxrsUri(config, "/api/user/" + userId + "/model/" + projId + "/status");
			res = withClient(uri, target -> target.request()
					.header("Authorization", "Bearer " + accessToken)
					.get());
			
			if (res.getStatus() == 303) {
				uri = URI.create(res.getHeaderString("Location"));
				res = withClient(uri, target -> target.request()
						.header("Authorization", "Bearer " + accessToken)
						.get());
			}
			
			if (res.getStatus() == 200) {
				MavenBuildHandle buildHandle = res.readEntity(MavenBuildHandle.class);
				buildStatusMap.put(projId, buildHandle.getStatus());
			}
		}
		
        return "template/model-list.vm";
	}

	@Override
	protected void handleActionRequest(HttpServerExchange exchange, VelocityContext context, Session session) throws Exception {
		
		UserTokens tokens = getAttribute(session, UserTokens.class);
		String accessToken = tokens.accessToken;
		String userId = tokens.userId;
		
		MultivaluedMap<String, String> reqprms = getRequestParameters(exchange);
		String modelId = reqprms.getFirst("modelId");
		String action = reqprms.getFirst("action");
		AssertState.notNull(modelId, "Null modelId");
		AssertState.notNull(action, "Null action");
		
		List<String> supported = Arrays.asList("build", "download");
		AssertState.isTrue(supported.contains(action), "Supported actions are: " + supported);
		
		session.removeAttribute("projId");
		
		if (action.equals("build")) {
			
			// Build Model
			
			// GET http://localhost:8200/jaxrs/api/user/{userId}/model/{modelId}/{runtime}/build
			// 
			
			String runtime = reqprms.getFirst("runtime");
			
			supported = Arrays.asList("standalone");
			AssertState.isTrue(supported.contains(runtime), "Supported runtimes are: " + supported);
			
			String projId = modelId + "/" + runtime;
			
			URI uri = ApiUtils.jaxrsUri(config, "/api/user/" + userId + "/model/" + projId + "/build");
			Response res = withClient(uri, target -> target.request()
					.header("Authorization", "Bearer " + accessToken)
					.get());
		
			assertStatus(res, Status.OK);
			
			session.setAttribute("projId", projId);
		}

		else if (action.equals("download")) {
			
			// Download the Target File
			
			// GET http://localhost:8100/maven/api/build/{projId}/download
			//
			
			String runtime = reqprms.getFirst("runtime");
			
			supported = Arrays.asList("standalone");
			AssertState.isTrue(supported.contains(runtime), "Supported runtimes are: " + supported);
			
			String projId = modelId + "/" + runtime;
			session.setAttribute("projId", projId);
			
			URI uri = ApiUtils.jaxrsUri(config, "/api/user/" + userId + "/model/" + projId + "/download");
			Response res = withClient(uri, target -> target.request()
					.header("Authorization", "Bearer " + accessToken)
					.get());
		
			assertStatus(res, Status.OK, Status.SEE_OTHER);
			
			if (res.getStatus() == 303) {
				String location = res.getHeaderString("Location");
		        new RedirectHandler(location).handleRequest(exchange);
		        return;
			}
		}

		redirectTo(exchange, "/user/models");
	}
}
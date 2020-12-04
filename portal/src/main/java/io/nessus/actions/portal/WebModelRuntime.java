package io.nessus.actions.portal;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.velocity.VelocityContext;

import io.nessus.actions.core.utils.ApiUtils;
import io.nessus.actions.jaxrs.type.Model;
import io.nessus.actions.jaxrs.type.Model.TargetRuntime;
import io.nessus.actions.jaxrs.type.UserTokens;
import io.nessus.common.AssertState;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;

public class WebModelRuntime extends AbstractUserResource {

	@Override
	protected String handlePageRequest(HttpServerExchange exchange, VelocityContext context, Session session) throws Exception {

		UserTokens tokens = getAttribute(session, UserTokens.class);
		String accessToken = tokens.accessToken;
		String userId = tokens.userId;
		
		MultivaluedMap<String, String> reqprms = getRequestParameters(exchange);
		TargetRuntime runtime = TargetRuntime.valueOf(reqprms.getFirst("runtime"));
		String modelId = reqprms.getFirst("modelId");
		
		// Get Model
		
		// GET http://localhost:8200/jaxrs/api/user/{userId}/model/{modelId}

		URI uri = ApiUtils.jaxrsUri(config, "/api/user/" + userId + "/model/" + modelId);
		Response res = withClient(uri, target -> target.request()
				.header("Authorization", "Bearer " + accessToken)
				.get());
	
		assertStatus(res, Status.OK);
		
		Model model = res.readEntity(Model.class);
		
		context.put("runtime", runtime);
		context.put("model", model);

		return "template/model-runtime.vm";
	}


	@Override
	protected void handleActionRequest(HttpServerExchange exchange, VelocityContext context, Session session) throws Exception {
		
		UserTokens tokens = getAttribute(session, UserTokens.class);
		String accessToken = tokens.accessToken;
		String userId = tokens.userId;
		
		MultivaluedMap<String, String> reqprms = getRequestParameters(exchange);
		String modelId = reqprms.getFirst("modelId");
		String action = reqprms.getFirst("action");
		String runtime = reqprms.getFirst("runtime");
		AssertState.notNull(modelId, "Null modelId");
		AssertState.notNull(action, "Null action");
		AssertState.notNull(runtime, "Null runtime");
		
		List<String> supported = Arrays.asList("build", "download");
		AssertState.isTrue(supported.contains(action), "Supported actions are: " + supported);
		
		session.removeAttribute("projId");
		
		if (action.equals("build")) {
			
			// Build Model
			
			// GET http://localhost:8200/jaxrs/api/user/{userId}/model/{modelId}/{runtime}/build
			// 
			
			supported = Arrays.asList("standalone");
			AssertState.isTrue(supported.contains(runtime), "Supported runtimes are: " + supported);
			
			String projId = modelId + "/" + runtime;
			
			URI uri = ApiUtils.jaxrsUri(config, "/api/user/" + userId + "/model/" + projId + "/build");
			Response res = withClient(uri, target -> target.request()
					.header("Authorization", "Bearer " + accessToken)
					.get());
		
			assertStatus(res, Status.OK);
			
			session.setAttribute("projId", projId);
			
			redirectTo(exchange, "/user/model/runtime?modelId=" + modelId + "&runtime=" + runtime);
		}

		else if (action.equals("download")) {
			
			// Download the Target File
			
			// GET http://localhost:8100/maven/api/build/{projId}/download
			//
			
			String projId = modelId + "/" + runtime;
			session.setAttribute("projId", projId);
			
			URI uri = ApiUtils.jaxrsUri(config, "/api/user/" + userId + "/model/" + projId + "/download");
			Response res = withClient(uri, target -> target.request()
					.header("Authorization", "Bearer " + accessToken)
					.get());
	        
			assertStatus(res, Status.OK);
			
			String contentType = res.getHeaderString("Content-Type");
			String contentDisposition = res.getHeaderString("Content-Disposition");
			InputStream inputStream = res.readEntity(InputStream.class);
			
			AssertState.notNull(contentType, "Null Content-Type");
			AssertState.notNull(contentDisposition, "Null Content-Disposition");
			AssertState.notNull(inputStream, "Null imput stream");
			
	        AssertState.isTrue(exchange.isInIoThread(), "Exchange not in I/O thread");

	        exchange.startBlocking();
	        
	        exchange.dispatch(dispex -> {
	        	
				HeaderMap responseHeaders = exchange.getResponseHeaders();
				responseHeaders.put(new HttpString("Content-Disposition"), contentDisposition);
				responseHeaders.put(new HttpString("Content-Type"), contentType);
	    		
    			try (OutputStream outputStream = exchange.getOutputStream()) {
    				
    				byte[] buffer = new byte[1024 * 1024];
    				
    				int len = inputStream.read(buffer);
    				int total = len;
    				
    				while (len > 0) {
    					outputStream.write(buffer, 0, len);
    					LOG.info("{} wrote {} bytes", Thread.currentThread(), total);
    					total += len = inputStream.read(buffer);
    				}
    			}
	        });
			
		} else {
			
			throw new UnsupportedOperationException("Unsupported action: " + action);
		}
	}
}
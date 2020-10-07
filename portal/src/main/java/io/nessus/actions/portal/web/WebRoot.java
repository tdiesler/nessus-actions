package io.nessus.actions.portal.web;

import static io.nessus.actions.portal.api.ApiUtils.portalUrl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.Map;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.nessus.actions.portal.PortalConfig;
import io.nessus.actions.portal.PortalMain;
import io.nessus.actions.portal.api.ApiUserRegister.User;
import io.nessus.common.AssertArg;
import io.nessus.common.AssertState;
import io.nessus.common.ConfigSupport;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.util.Headers;

public class WebRoot extends ConfigSupport<PortalConfig> implements HttpHandler {
	
	private final HttpHandler httpHandler;
    private final VelocityEngine ve;
    
	public WebRoot(PortalConfig config) {
		super(config);
		
		ClassLoader loader = WebRoot.class.getClassLoader();
		httpHandler = new ResourceHandler(new ClassPathResourceManager(loader));

        ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();
	}

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		
        String relPath = exchange.getRelativePath();
        
        // Redirect Home
        
        if (relPath.length() < 2) {
        	redirectHome(exchange);
        	return;
        }
        
        // Serve static content
        
        if (!relPath.startsWith("/web")) {
        	staticContent(exchange);
        	return;
        }
        
        // Serve dynamic content
        
        ByteBuffer content = dynamicContent(exchange);

        if (content == null) {
        	logError("No content for: {}", relPath);
        	return;
        }

        exchange.getResponseSender().send(content);
	}

	private void staticContent(HttpServerExchange exchange) throws Exception {
		httpHandler.handleRequest(exchange);
	}

	private ByteBuffer dynamicContent(HttpServerExchange exchange) throws Exception {

        String relPath = exchange.getRelativePath();

        String tmplPath = null;
        VelocityContext context = new VelocityContext();
        context.put("implVersion", PortalMain.getImplVersion());
        context.put("implBuild", PortalMain.getImplBuild());

        try {

            if (relPath.equals("/web")) {
                
            	tmplPath = pageHome(exchange, context);
                
            } else if (relPath.equals("/web/users/register")) {
            	
            	tmplPath = actUsersRegister(exchange, context);
            	
            } else {
            	
            	throw new ResourceNotFoundException("Resource not found: " + relPath); 
            }

        } catch (Exception ex) {

            logError("Error", ex);
            tmplPath = pageError(context, ex);
        }

        if (tmplPath == null) {
        	logError("No template for: {}", relPath);
        	return null;
        }
        
        // Read template and pass through velocity
        
        exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "text/html");

        ClassLoader loader = getClass().getClassLoader();
		InputStream input = loader.getResourceAsStream(tmplPath);
		AssertState.notNull(input, "Null resource: " + tmplPath);
		
		try (InputStreamReader reader = new InputStreamReader(input)) {

            StringWriter strwr = new StringWriter();
            ve.evaluate(context, strwr, tmplPath, reader);

            return ByteBuffer.wrap(strwr.toString().getBytes());
        }
	}

    private String actUsersRegister(HttpServerExchange exchange, VelocityContext context) {

        Map<String, Deque<String>> qparams = exchange.getQueryParameters();
        String firstName = qparams.get("firstName").getFirst();
        String lastName = qparams.get("lastName").getFirst();
        String email = qparams.get("email").getFirst();
        String username = qparams.get("username").getFirst();
        String password = qparams.get("password").getFirst();
        String retype = qparams.get("retype").getFirst();
        
        AssertArg.isEqual(password, retype, "Password does not match");
        
        User user = new User(firstName, lastName, email, username, password);
        
		Response res = ClientBuilder.newClient()
				.target(portalUrl("/api/users"))
				.request().post(Entity.json(user));
		
		int status = res.getStatus();
		String reason = res.getStatusInfo().getReasonPhrase();
        context.put("msg", String.format("%s %s", status, reason));
        
        return "template/portal-msg.vm";
    }

	private String pageHome(HttpServerExchange exchange, VelocityContext context) throws Exception {
		
		URL resUrl = getClass().getResource("/json/user-register.json");
		
		ObjectMapper mapper = new ObjectMapper();
		User user = mapper.readValue(resUrl, User.class);
        context.put("user", user);
		
        return "template/portal-home.vm";
    }

    private String pageError(VelocityContext context, Throwable th) {

        String errmsg = th.getMessage();
        if (errmsg == null || errmsg.length() == 0) 
            errmsg = th.toString();
        
        context.put("errmsg", errmsg);

        return "template/portal-error.vm";
    }

	private void redirectHome(HttpServerExchange exchange) throws Exception {
        new RedirectHandler("/portal/web").handleRequest(exchange);
    }

}
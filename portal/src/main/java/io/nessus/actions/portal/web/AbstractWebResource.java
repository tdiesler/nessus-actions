package io.nessus.actions.portal.web;

import static io.nessus.actions.portal.api.ApiUtils.hasStatus;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import io.nessus.actions.portal.AbstractResource;
import io.nessus.actions.portal.PortalMain;
import io.nessus.actions.portal.api.PortalApi;
import io.nessus.actions.portal.service.SessionManagerService;
import io.nessus.common.AssertArg;
import io.nessus.common.AssertState;
import io.nessus.common.CheckedExceptionWrapper;
import io.undertow.io.Receiver;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.server.session.Session;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import io.undertow.util.StatusCodes;

abstract class AbstractWebResource extends AbstractResource implements HttpHandler {
	
	protected final HttpHandler httpHandler;
    
	AbstractWebResource() {

		if (!RuntimeSingleton.isInitialized()) {
	        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
	        Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
	        Velocity.init();
		}
		
		ClassLoader loader = WebRoot.class.getClassLoader();
		httpHandler = new ResourceHandler(new ClassPathResourceManager(loader));
	}

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		
		// Remove the root context
		
		String reqPath = exchange.getRequestPath();
		if (reqPath.startsWith("/portal"))
			reqPath = reqPath.substring(7);
        
        // Redirect home
        
        if (reqPath.length() <= "/web".length()) {
        	redirectToHome(exchange);
        	return;
        }
        
        // Static content
        
        if (!reqPath.startsWith("/web")) {
        	staticContent(exchange);
        	return;
        }
        
        // Dynamic content
        
        VelocityContext context = new VelocityContext();
        context.put("implVersion", PortalMain.getImplVersion());
        context.put("implBuild", PortalMain.getImplBuild());

        String tmplPath = null;
        
        try {

            if (reqPath.endsWith("/act")) {
            	
            	handleActionRequest(exchange, context);
            	
            	int status = exchange.getStatusCode();
            	AssertState.isEqual(StatusCodes.FOUND, status, "Expected redirect after: " + reqPath);
            	return;
            }

        	tmplPath = handlePageRequest(exchange, context);
        	
        } catch (Exception ex) {

            logError(ex);
            tmplPath = errorPage(context, ex, null);
        }

        // Read template and pass through velocity
        
    	AssertState.notNull(tmplPath, "No template for: " + reqPath);
    	
        ClassLoader loader = PortalApi.class.getClassLoader();
		InputStream input = loader.getResourceAsStream(tmplPath);
		AssertState.notNull(input, "Null resource: " + tmplPath);
		
        exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "text/html");
		
		try (InputStreamReader reader = new InputStreamReader(input)) {

            StringWriter strwr = new StringWriter();
            Velocity.evaluate(context, strwr, tmplPath, reader);

            ByteBuffer content = ByteBuffer.wrap(strwr.toString().getBytes());
            exchange.getResponseSender().send(content);
        }
	}

	protected String handlePageRequest(HttpServerExchange exchange, VelocityContext context) throws Exception {
		return null;
	}

	protected void handleActionRequest(HttpServerExchange exchange, VelocityContext context) throws Exception {
	}

	protected MultivaluedMap<String, String> getRequestParameters(HttpServerExchange exchange) {
		
		MultivaluedMap<String, String> qparams = new MultivaluedHashMap<String, String>();

		BiConsumer<String, String> put = (k, v) -> {
			List<String> values = qparams.get(k);
			if (values == null) {
				values = new ArrayList<String>();
				qparams.put(k, values);
			}
			values.add(v);
		};
		
		HttpString method = exchange.getRequestMethod();
		
		if (method.equals(Methods.GET)) {
			
			exchange.getQueryParameters().forEach((key, vals) -> 
				vals.forEach(val -> put.accept(key, val)));
			
		} else if (method.equals(Methods.POST)) {
			
			Receiver receiver = exchange.getRequestReceiver();
			receiver.receiveFullString((ex, msg) -> {
				Arrays.asList(msg.split("&")).forEach(param -> {
					int idx = param.indexOf('=');
					String key = param.substring(0, idx);
					String val = decode(param.substring(idx + 1));
					put.accept(key, val);
				});
			});
			
		} else {
			
			throw new UnsupportedOperationException("Unsupported method: " + method);
		}
		
		return qparams;
	}

	protected String decode(String value) {
		try {
			return URLDecoder.decode(value, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			throw CheckedExceptionWrapper.create(ex);
		}
	}
	
	protected void staticContent(HttpServerExchange exchange) throws Exception {
		httpHandler.handleRequest(exchange);
	}

	protected void assertStatus(Response res, Status... exp) {
		if (!hasStatus(res, exp)) {
			int status = res.getStatus();
			String reason = res.getStatusInfo().getReasonPhrase();
			throw new IllegalStateException(String.format("[%d %s]", status, reason));
		}
	}
	
	protected String errorPage(VelocityContext context, Throwable th, String errmsg) {

		if (errmsg == null)
			errmsg = th.getMessage();
		
        if (errmsg != null && errmsg.length() == 0) 
            errmsg = th.toString();
        
        context.put("errmsg", errmsg);

        return "template/portal-error.vm";
    }
    
	protected String infoPage(VelocityContext context, String msg) {

        context.put("msg", msg);

        return "template/portal-info.vm";
    }
    
	protected void redirectToHome(HttpServerExchange exchange) throws Exception {
		redirectTo(exchange, "/home");
    }

	protected void redirectToLogin(HttpServerExchange exchange) throws Exception {
		redirectTo(exchange, "/login");
    }

	protected void redirectTo(HttpServerExchange exchange, String path) throws Exception {
        new RedirectHandler("/portal/web" + path).handleRequest(exchange);
    }

	protected Session getSession(HttpServerExchange exchange, boolean create) {
		SessionManagerService sessions = config.getService(SessionManagerService.class);
		Session session = sessions.getSession(exchange, create);
		return session;
	}
	
	@SuppressWarnings("unchecked")
	protected <T> void setAttribute(Session session, T value) {
		AssertArg.notNull(session, "Null session");
		AssertArg.notNull(value, "Null value");
		setAttribute(session, (Class<T>) value.getClass(), value);
	}

	protected <T> void setAttribute(Session session, Class<T> type, T value) {
		AssertArg.notNull(session, "Null session");
		AssertArg.notNull(type, "Null type");
		AssertArg.notNull(value, "Null value");
		setAttribute(session, type.getSimpleName(), value);
	}

	protected <T> void setAttribute(Session session, String name, T value) {
		AssertArg.notNull(session, "Null session");
		AssertArg.notNull(name, "Null name");
		AssertArg.notNull(value, "Null value");
		session.setAttribute(name, value);
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T getAttribute(Session session, Class<T> type) {
		if (session == null) return null;
		AssertArg.notNull(type, "Null type");
		return (T) session.getAttribute(type.getSimpleName());
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T getAttribute(Session session, String name, Class<T> type) {
		if (session == null) return null;
		AssertArg.notNull(name, "Null name");
		AssertArg.notNull(type, "Null type");
		return (T) session.getAttribute(name);
	}
}
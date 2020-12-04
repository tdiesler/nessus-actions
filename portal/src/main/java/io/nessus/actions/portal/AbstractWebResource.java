package io.nessus.actions.portal;

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

import io.nessus.actions.core.jaxrs.AbstractResource;
import io.nessus.actions.core.utils.ApiUtils;
import io.nessus.actions.core.utils.ApiUtils.ErrorMessage;
import io.nessus.actions.portal.main.PortalMain;
import io.nessus.actions.portal.service.SessionService;
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

public abstract class AbstractWebResource extends AbstractResource implements HttpHandler {
	
	protected final ResourceHandler resourceHandler;
    
	protected AbstractWebResource() {

		if (!RuntimeSingleton.isInitialized()) {
	        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
	        Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
	        Velocity.init();
		}
		
		ClassLoader loader = WebRoot.class.getClassLoader();
		resourceHandler = new ResourceHandler(new ClassPathResourceManager(loader));
	}

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		
		String reqPath = exchange.getRequestPath();
		String relPath = exchange.getRelativePath();
		
        VelocityContext context = new VelocityContext();
        context.put("implVersion", PortalMain.getImplVersion());
        context.put("implBuild", PortalMain.getImplBuild());

        // Redirect home
        
        if (reqPath.equals("/")) {
        	new RedirectHandler("/portal").handleRequest(exchange);
        	return;
        }
        
        // Static content
        
        if (relPath.startsWith("/static") || reqPath.equals("/favicon.ico")) {
        	staticContent(exchange);
        	return;
        }
        
        // Dynamic content
        
        String tmplPath = null;
        
        try {

            if (reqPath.endsWith("/act")) {
            	
            	handleActionRequest(exchange, context);
            	
            	// Actions should always redirect
            	
            	if (!exchange.isDispatched()) {
                	int status = exchange.getStatusCode();
                	AssertState.isEqual(StatusCodes.FOUND, status, "Expected redirect after: " + reqPath);
            	}
            	
            	return;
            	
            } else {
            	
            	tmplPath = handlePageRequest(exchange, context);

            	// Page handler redirects, nothing more to do
            	
            	int status = exchange.getStatusCode();
                if (tmplPath == null && status == StatusCodes.FOUND)
                	return;
            }
            
        } catch (Exception ex) {

            logError(ex);
            tmplPath = errorPage(context, ex, null);
        }

        // Read template and pass through velocity
        
    	AssertState.notNull(tmplPath, "No template for: " + reqPath);
    	
        ClassLoader loader = PortalMain.class.getClassLoader();
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
		throw new UnsupportedOperationException(exchange.getRequestPath());
	}

	protected void handleActionRequest(HttpServerExchange exchange, VelocityContext context) throws Exception {
		throw new UnsupportedOperationException(exchange.getRequestPath());
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
					String val = urlDecode(param.substring(idx + 1));
					put.accept(key, val);
				});
			});
			
		} else {
			
			throw new UnsupportedOperationException("Unsupported method: " + method);
		}
		
		return qparams;
	}

	protected String urlDecode(String value) {
		try {
			return URLDecoder.decode(value, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			throw CheckedExceptionWrapper.create(ex);
		}
	}
	
	protected void staticContent(HttpServerExchange exchange) throws Exception {
		resourceHandler.handleRequest(exchange);
	}

	protected void assertStatus(Response res, Status... exp) {
		if (!ApiUtils.hasStatus(res, exp)) {
			int status = res.getStatus();
			String reason = res.getStatusInfo().getReasonPhrase();
			ErrorMessage errmsg = ApiUtils.getErrorMessage(res);
			if (errmsg != null) {
				throw new IllegalStateException(String.format("[%d %s] - %s", status, reason, errmsg));
			} else {
				throw new IllegalStateException(String.format("[%d %s]", status, reason));
			}
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
    
	protected void redirectHome(HttpServerExchange exchange) throws Exception {
		redirectTo(exchange, "/");
    }

	protected void redirectToLogin(HttpServerExchange exchange) throws Exception {
		redirectTo(exchange, "/user/login");
    }

	protected void redirectTo(HttpServerExchange exchange, String path) throws Exception {
        new RedirectHandler("/portal" + path).handleRequest(exchange);
    }

	protected Session getSession(HttpServerExchange exchange, boolean create) {
		SessionService sessions = config.getService(SessionService.class);
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
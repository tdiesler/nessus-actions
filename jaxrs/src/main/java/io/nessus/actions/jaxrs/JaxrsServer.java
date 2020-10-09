package io.nessus.actions.jaxrs;

import javax.net.ssl.SSLContext;
import javax.ws.rs.core.Application;

import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;

import io.undertow.Undertow;
import io.undertow.Undertow.Builder;
import io.undertow.server.HttpHandler;

// UndertowJaxrsServer is not sufficiently generic
public class JaxrsServer extends UndertowJaxrsServer {

	private SSLContext sslContext;
	private String hostname;
	private Integer httpsPort;
	private Integer httpPort;

	@Override
	public JaxrsServer setHostname(String hostname) {
		this.hostname = hostname;
		return this;
	}

	public JaxrsServer setHttpPort(int port) {
		this.httpPort = port;
		return this;
	}

	public JaxrsServer setHttpsPort(int port, SSLContext sslContext) {
		this.sslContext = sslContext;
		this.httpsPort = port;
		return this;
	}

	@Override
	public JaxrsServer start() {
		
		Builder builder = Undertow.builder()
			.addHttpListener(httpPort, hostname)
			.setHandler(root);
		
		if (httpsPort != null) {
			builder.addHttpsListener(httpsPort, hostname, sslContext);
		}
		
		server = builder.build();
		server.start();
		
		return this;
	}

	@Override
	public JaxrsServer setPort(int port) {
		return setHttpPort(port);
	}

	public JaxrsServer deployApplication(Class<? extends Application> application) {
		super.deployOldStyle(application);
		return this;
	}

	public JaxrsServer addPrefixPath(String path, HttpHandler handler) {
		root.addPrefixPath(path, handler);
		return this;
	}
}
package io.nessus.actions.portal;

import java.io.IOException;
import java.net.URL;

import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;

import io.nessus.actions.portal.api.ApiService;
import io.nessus.actions.portal.web.WebRoot;
import io.nessus.common.main.AbstractMain;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;

public class PortalMain extends AbstractMain<PortalConfig, PortalOptions> {

    public static void main(String... args) throws Exception {

    	PortalConfig config = PortalConfig.createConfig();
    	
    	new PortalMain(config)
    		.start(args);
    }

    public PortalMain(PortalConfig config) throws IOException {
        super(config);
        config.addService(new ApiService(config));
    }

    @Override
    protected PortalOptions createOptions() {
        return new PortalOptions();
    }

	@Override
    protected void doStart(PortalOptions options) throws Exception {
        
    	String portalUrl = config.getPortalUrl();
    	
        logInfo("***************************************************");
        logInfo("Starting Portal {}", portalUrl);
        logInfo("Version {}", getVersionString());
        logInfo("***************************************************");
        logInfo();
        
        startPortalServer();
    }

	public UndertowJaxrsServer startPortalServer() throws Exception {
		
		URL url = new URL(config.getPortalUrl());
		
		// Undertow needs to bind to 0.0.0.0 in docker

		String hostname = url.getHost();
		if (hostname.equals("localhost") || hostname.equals("127.0.0.1")) {
			hostname = "0.0.0.0";
			logDebug("Binding to {}", hostname);
		}
		
		JaxrsServer server = new JaxrsServer()
				.setHostname(hostname)
				.setPort(url.getPort())
				.start();
		
		server.deployOldStyle(PortalApi.class);
		server.addPrefixPath("/portal", new WebRoot(config));
		
		return server;
	}
	
	// UndertowJaxrsServer is not sufficiently generic
	class JaxrsServer extends UndertowJaxrsServer {
		
		@Override
		public JaxrsServer setHostname(String hostname) {
			return (JaxrsServer) super.setHostname(hostname);
		}

		@Override
		public JaxrsServer setPort(int port) {
			return (JaxrsServer) super.setPort(port);
		}

		@Override
		public JaxrsServer start() {
			return (JaxrsServer) super.start();
		}

		public PathHandler addPrefixPath(String path, HttpHandler handler) {
			return root.addPrefixPath(path, handler);
		}
	}
}

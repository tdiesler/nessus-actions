package io.nessus.actions.portal;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.net.ssl.SSLContext;

import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;

import io.nessus.actions.portal.api.PortalApi;
import io.nessus.actions.portal.service.ApiService;
import io.nessus.actions.portal.utils.SSLContextBuilder;
import io.nessus.actions.portal.web.WebHome;
import io.nessus.actions.portal.web.WebRoot;
import io.nessus.actions.portal.web.WebUserDelete;
import io.nessus.actions.portal.web.WebUserLogin;
import io.nessus.actions.portal.web.WebUserLogout;
import io.nessus.actions.portal.web.WebUserRegister;
import io.nessus.actions.portal.web.WebUserStatus;
import io.nessus.common.main.AbstractMain;
import io.undertow.Undertow;
import io.undertow.Undertow.Builder;
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
        
        JaxrsServer server = createJaxrsServer();
        server.start();
    }

	public JaxrsServer createJaxrsServer() throws Exception {
		
		URL url = new URL(config.getPortalUrl());

		// Undertow needs to bind to 0.0.0.0 in Docker
		// At least for now

		String hostname = "0.0.0.0";
		int port = url.getPort();
		
		JaxrsServer server = new JaxrsServer()
				.setHostname(hostname)
				.setHttpPort(port);
		
		if (isTLSEnabled()) {
			
			String alias = "tryit";
			Integer tlsPort = config.getPortalTLSPort();
			Path tlsKey = Paths.get(config.getPortalTLSKey());
			Path tlsCrt = Paths.get(config.getPortalTLSCrt());
			
			SSLContext sslContext = new SSLContextBuilder()
					.keystorePath(Paths.get("/tmp/keystore.jks"))
					.addCertificate(alias, tlsCrt)
					.addPrivateKey(alias, tlsKey)
					.build();
			
			SSLContext.setDefault(sslContext);
			server.setHttpsPort(tlsPort, sslContext);
		}
		
		server.deployOldStyle(PortalApi.class);
		server.addPrefixPath("/portal", new WebRoot());
		server.addPrefixPath("/portal/web/home", new WebHome());
		server.addPrefixPath("/portal/web/delete", new WebUserDelete());
		server.addPrefixPath("/portal/web/login", new WebUserLogin());
		server.addPrefixPath("/portal/web/logout", new WebUserLogout());
		server.addPrefixPath("/portal/web/register", new WebUserRegister());
		server.addPrefixPath("/portal/web/status", new WebUserStatus());
		
		return server;
	}
	
	private boolean isTLSEnabled() {
		Integer tlsPort = config.getPortalTLSPort();
		String tlsCert = config.getPortalTLSCrt();
		String tlsKey = config.getPortalTLSKey();
		logInfo("TLS Port: {}", tlsPort);
		logInfo("TLS Crt: {}", tlsCert);
		logInfo("TLS Key: {}", tlsKey);
		if (tlsPort == null || tlsCert == null || tlsKey == null) {
			return false;
		}
		if (!Paths.get(tlsCert).toFile().isFile()) {
			logError("Cannot find TLS Cert: {}", tlsCert);
			return false;
		}
		if (!Paths.get(tlsKey).toFile().isFile()) {
			logError("Cannot find TLS Key: {}", tlsKey);
			return false;
		}
		logInfo("TLS Enabled");
		return true;
	}

	// UndertowJaxrsServer is not sufficiently generic
	public static class JaxrsServer extends UndertowJaxrsServer {

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

		public PathHandler addPrefixPath(String path, HttpHandler handler) {
			return root.addPrefixPath(path, handler);
		}
	}
}

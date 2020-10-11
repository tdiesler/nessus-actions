package io.nessus.actions.portal;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.net.ssl.SSLContext;

import io.nessus.actions.jaxrs.JaxrsConfig;
import io.nessus.actions.jaxrs.JaxrsServer;
import io.nessus.actions.jaxrs.main.JaxrsOptions;
import io.nessus.actions.jaxrs.service.KeycloakService;
import io.nessus.actions.jaxrs.utils.SSLContextBuilder;
import io.nessus.actions.portal.web.WebHome;
import io.nessus.actions.portal.web.WebRoot;
import io.nessus.actions.portal.web.WebUserDelete;
import io.nessus.actions.portal.web.WebUserLogin;
import io.nessus.actions.portal.web.WebUserLogout;
import io.nessus.actions.portal.web.WebUserRegister;
import io.nessus.actions.portal.web.WebUserStatus;
import io.nessus.common.main.AbstractMain;

public class PortalMain extends AbstractMain<JaxrsConfig, JaxrsOptions> {

    public static void main(String... args) throws Exception {

    	JaxrsConfig config = JaxrsConfig.createConfig();
    	
    	new PortalMain(config)
    		.start(args);
    }

    public PortalMain(JaxrsConfig config) throws IOException {
        super(config);
        config.addService(new KeycloakService(config));
    }

    @Override
    protected JaxrsOptions createOptions() {
        return new JaxrsOptions();
    }

	@Override
    protected void doStart(JaxrsOptions options) throws Exception {
        
    	String portalUrl = config.getJaxrsUrl();
    	
        logInfo("***************************************************");
        logInfo("Starting Portal {}", portalUrl);
        logInfo("Version {}", getVersionString());
        logInfo("***************************************************");
        logInfo();
        
    }

	public JaxrsServer createJaxrsServer() throws Exception {
		
		URL url = new URL(config.getJaxrsUrl());

		// Undertow needs to bind to 0.0.0.0 in Docker
		// At least for now

		String hostname = "0.0.0.0";
		int port = url.getPort();
		
		JaxrsServer server = new JaxrsServer()
				.setHostname(hostname)
				.setHttpPort(port);
		
		if (isTLSEnabled()) {
			
			String alias = "tryit";
			Path tlsKey = Paths.get(config.getJaxrsTLSKey());
			Path tlsCrt = Paths.get(config.getJaxrsTLSCrt());
			
			SSLContext sslContext = new SSLContextBuilder()
					.keystorePath(Paths.get("/tmp/keystore.jks"))
					.addCertificate(alias, tlsCrt)
					.addPrivateKey(alias, tlsKey)
					.build();
			
			SSLContext.setDefault(sslContext);
			
			URL tlsUrl = new URL(config.getJaxrsTLSUrl());
			int tlsPort = tlsUrl.getPort();
			
			server.setHttpsPort(tlsPort, sslContext);
		}
		
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
		String tlsUrl = config.getJaxrsTLSUrl();
		String tlsCert = config.getJaxrsTLSCrt();
		String tlsKey = config.getJaxrsTLSKey();
		if (tlsUrl == null || tlsCert == null || tlsKey == null) {
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
		logInfo("TLS URL: {}", tlsUrl);
		logInfo("TLS Crt: {}", tlsCert);
		logInfo("TLS Key: {}", tlsKey);
		logInfo("TLS Enabled");
		return true;
	}
}

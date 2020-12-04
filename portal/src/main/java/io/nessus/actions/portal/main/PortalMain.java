package io.nessus.actions.portal.main;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.net.ssl.SSLContext;

import io.nessus.actions.core.NessusConfig;
import io.nessus.actions.core.service.KeycloakService;
import io.nessus.actions.portal.WebHome;
import io.nessus.actions.portal.WebModelCreate;
import io.nessus.actions.portal.WebModelDelete;
import io.nessus.actions.portal.WebModelList;
import io.nessus.actions.portal.WebModelRuntime;
import io.nessus.actions.portal.WebModelUpdate;
import io.nessus.actions.portal.WebRoot;
import io.nessus.actions.portal.WebUserDelete;
import io.nessus.actions.portal.WebUserHome;
import io.nessus.actions.portal.WebUserLogin;
import io.nessus.actions.portal.WebUserLogout;
import io.nessus.actions.portal.WebUserState;
import io.nessus.actions.portal.service.SessionService;
import io.nessus.common.main.AbstractMain;
import io.nessus.common.rest.JaxrsServer;
import io.nessus.common.rest.SSLContextBuilder;

public class PortalMain extends AbstractMain<NessusConfig, PortalOptions> {

    public static void main(String... args) throws Exception {

    	NessusConfig config = NessusConfig.createConfig();
    	
    	new PortalMain(config)
    		.start(args);
    }

    public PortalMain(NessusConfig config) throws IOException {
        super(config);
        config.addService(new KeycloakService(config));
        config.addService(new SessionService(config));
    }

    @Override
    protected PortalOptions createOptions() {
        return new PortalOptions();
    }

	@Override
    protected void doStart(PortalOptions options) throws Exception {
        
    	String portalUrl = config.getPortalUrl();
    	String portalTLSUrl = config.getPortalTLSUrl();
    	
    	boolean withTLS = isTLSEnabled();
    	
        logInfo("***************************************************");
        if (withTLS) logInfo("Starting {}", portalTLSUrl);
        logInfo("Starting {}", portalUrl);
        logInfo("Version {}", getVersionString());
        logInfo("***************************************************");
        logInfo();
        
        JaxrsServer server = createJaxrsServer(withTLS);
        server.start();
    }

	public JaxrsServer createJaxrsServer() throws Exception {
		return createJaxrsServer(isTLSEnabled());
	}
	
	private JaxrsServer createJaxrsServer(boolean withTLS) throws Exception {
		
		URL url = new URL(config.getPortalUrl());

		// Undertow needs to bind to 0.0.0.0 in Docker
		// At least for now

		String host = "0.0.0.0";
		int port = url.getPort();
		
		JaxrsServer server = new JaxrsServer(getConfig())
				.withHostname(host)
				.withHttpPort(port);
		
		if (withTLS) {
			
			String alias = "nessus-actions-jaxrs";
			Path tlsKey = Paths.get(config.getTLSKey());
			Path tlsCrt = Paths.get(config.getTLSCrt());
			
			SSLContext sslContext = new SSLContextBuilder()
					.keystorePath(Paths.get("/tmp/keystore.jks"))
					.addCertificate(alias, tlsCrt)
					.addPrivateKey(alias, tlsKey)
					.build();
			
			SSLContext.setDefault(sslContext);
			
			URL tlsUrl = new URL(config.getJaxrsTLSUrl());
			int tlsPort = tlsUrl.getPort();
			
			server.withHttpsPort(tlsPort, sslContext);
		}
		
		server.addPrefixPath("/", new WebRoot());
		server.addPrefixPath("/portal", new WebHome());
		server.addPrefixPath("/portal/user", new WebUserHome());
		server.addPrefixPath("/portal/user/login", new WebUserLogin());
		server.addPrefixPath("/portal/user/logout", new WebUserLogout());
		server.addPrefixPath("/portal/user/delete", new WebUserDelete());
		server.addPrefixPath("/portal/user/models", new WebModelList());
		server.addPrefixPath("/portal/user/models/create", new WebModelCreate());
		server.addPrefixPath("/portal/user/model/delete", new WebModelDelete());
		server.addPrefixPath("/portal/user/model/update", new WebModelUpdate());
		server.addPrefixPath("/portal/user/model/runtime", new WebModelRuntime());
		server.addPrefixPath("/portal/user/state", new WebUserState());
		
		return server;
	}
	
	private boolean isTLSEnabled() {
		String tlsUrl = config.getPortalTLSUrl();
		String tlsCert = config.getTLSCrt();
		String tlsKey = config.getTLSKey();
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

package io.nessus.actions.portal.main;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.net.ssl.SSLContext;

import io.nessus.actions.jaxrs.service.JaxrsService;
import io.nessus.actions.jaxrs.service.KeycloakService;
import io.nessus.actions.jaxrs.utils.SSLContextBuilder;
import io.nessus.actions.portal.WebUserLogin;
import io.nessus.actions.portal.WebRoot;
import io.nessus.actions.portal.WebUserDelete;
import io.nessus.actions.portal.WebUserHome;
import io.nessus.actions.portal.WebUserLogout;
import io.nessus.actions.portal.WebUserModelCreate;
import io.nessus.actions.portal.WebUserModelDelete;
import io.nessus.actions.portal.WebUserModelUpdate;
import io.nessus.actions.portal.WebUserModels;
import io.nessus.actions.portal.WebUserState;
import io.nessus.actions.portal.service.SessionService;
import io.nessus.common.main.AbstractMain;
import io.undertow.Undertow;
import io.undertow.Undertow.Builder;
import io.undertow.server.handlers.PathHandler;

public class PortalMain extends AbstractMain<PortalConfig, PortalOptions> {

    public static void main(String... args) throws Exception {

    	PortalConfig config = PortalConfig.createConfig();
    	
    	new PortalMain(config)
    		.start(args);
    }

    public PortalMain(PortalConfig config) throws IOException {
        super(config);
        config.addService(new JaxrsService(config));
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
        
        Undertow server = createUndertowServer(withTLS);
        server.start();
    }

	public Undertow createUndertowServer(boolean withTLS) throws Exception {
		
		URL url = new URL(config.getPortalUrl());

		// Undertow needs to bind to 0.0.0.0 in Docker
		// At least for now

		String host = "0.0.0.0";
		int port = url.getPort();
		
		PathHandler handler = new PathHandler();
		handler.addPrefixPath("/portal", new WebRoot(config));
		handler.addPrefixPath("/portal/user", new WebUserHome(config));
		handler.addPrefixPath("/portal/user/login", new WebUserLogin(config));
		handler.addPrefixPath("/portal/user/logout", new WebUserLogout(config));
		handler.addPrefixPath("/portal/user/delete", new WebUserDelete(config));
		handler.addPrefixPath("/portal/user/models", new WebUserModels(config));
		handler.addPrefixPath("/portal/user/models/create", new WebUserModelCreate(config));
		handler.addPrefixPath("/portal/user/model/update", new WebUserModelUpdate(config));
		handler.addPrefixPath("/portal/user/model/delete", new WebUserModelDelete(config));
		//handler.addPrefixPath("/portal/user/model/publish/eap", new WebUserModelPublishEAP(config));
		//handler.addPrefixPath("/portal/user/model/publish/docker", new WebUserModelPublishDocker(config));
		//handler.addPrefixPath("/portal/user/model/publish/openshift", new WebUserModelPublishOpenshift(config));
		//handler.addPrefixPath("/portal/user/model/publish/standalone", new WebUserModelPublishStandalone(config));
		handler.addPrefixPath("/portal/user/state", new WebUserState(config));
		
		Builder builder = Undertow.builder()
	         .addHttpListener(port, host, handler)
	         .setHandler(handler);
		
		if (isTLSEnabled()) {
			
			String alias = "nessus-actions-portal";
			Path tlsKey = Paths.get(config.getPortalTLSKey());
			Path tlsCrt = Paths.get(config.getPortalTLSCrt());
			
			SSLContext sslContext = new SSLContextBuilder()
					.keystorePath(Paths.get("/tmp/keystore.jks"))
					.addCertificate(alias, tlsCrt)
					.addPrivateKey(alias, tlsKey)
					.build();
			
			SSLContext.setDefault(sslContext);
			
			URL tlsUrl = new URL(config.getPortalTLSUrl());
			int tlsPort = tlsUrl.getPort();
			
			builder.addHttpsListener(tlsPort, host, sslContext, handler);
		}
		
		Undertow server = builder.build();
		return server;
	}
	
	private boolean isTLSEnabled() {
		String tlsUrl = config.getPortalTLSUrl();
		String tlsCert = config.getPortalTLSCrt();
		String tlsKey = config.getPortalTLSKey();
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

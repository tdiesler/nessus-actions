package io.nessus.actions.jaxrs.main;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.net.ssl.SSLContext;

import io.nessus.actions.core.NessusConfig;
import io.nessus.actions.core.service.KeycloakService;
import io.nessus.actions.jaxrs.JaxrsApplication;
import io.nessus.actions.jaxrs.service.DefaultMavenBuilderService;
import io.nessus.actions.jaxrs.service.UserModelService;
import io.nessus.actions.jaxrs.service.UserStateService;
import io.nessus.common.main.AbstractMain;
import io.nessus.common.rest.JaxrsServer;
import io.nessus.common.rest.SSLContextBuilder;
import io.nessus.h2.H2Config;

public class JaxrsMain extends AbstractMain<NessusConfig, JaxrsOptions> {

    public static void main(String... args) throws Exception {

    	NessusConfig config = NessusConfig.createConfig();
    	
    	new JaxrsMain(config)
    		.start(args);
    }

    public JaxrsMain(NessusConfig config) throws IOException {
        super(config);
		config.addService(new DefaultMavenBuilderService(config));
		config.addService(new KeycloakService(config));
		config.addService(new UserModelService(config));
		config.addService(new UserStateService(config));
    }

    @Override
    protected JaxrsOptions createOptions() {
        return new JaxrsOptions();
    }

	@Override
	protected void prepare(Map<String, String> mapping, JaxrsOptions options) {
		mapping.putAll(H2Config.PROPERTY_MAPPING);
		super.prepare(mapping, options);
	}

	@Override
    protected void doStart(JaxrsOptions options) throws Exception {
        
    	String jaxrsUrl = config.getJaxrsUrl();
    	String jaxrsTLSUrl = config.getJaxrsTLSUrl();
    	
    	boolean withTLS = isTLSEnabled();
    	
        logInfo("***************************************************");
        if (withTLS) logInfo("Starting {}", jaxrsTLSUrl);
        logInfo("Starting {}", jaxrsUrl);
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
		
		URL url = new URL(config.getJaxrsUrl());

		// Undertow needs to bind to 0.0.0.0 in Docker
		// At least for now

		String host = "0.0.0.0";
		int port = url.getPort();
		
		JaxrsServer server = new JaxrsServer(getConfig())
				.withHostname(host)
				.withHttpPort(port);
		
		if (withTLS) {
			
			Path pemPath = Paths.get("/etc/x509/https/jboss-org.pem");
			Path crtPath = Paths.get(config.getTLSCrt());
			Path keyPath = Paths.get(config.getTLSKey());
			
			SSLContext sslContext = new SSLContextBuilder()
					.keystorePath(Paths.get("/tmp/keystore.jks"))
					.addPem("jboss-org", pemPath)
					.addCertificate("jaxrs", crtPath)
					.addPrivateKey("jaxrs", keyPath)
					.build();
			
			SSLContext.setDefault(sslContext);
			
			URL tlsUrl = new URL(config.getJaxrsTLSUrl());
			int tlsPort = tlsUrl.getPort();
			
			server.withHttpsPort(tlsPort, sslContext);
		}
		
		server.deploy("/jaxrs/api", new JaxrsApplication(getConfig()));
		
		return server;
	}
	
	private boolean isTLSEnabled() {
		String tlsUrl = config.getJaxrsTLSUrl();
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

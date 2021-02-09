package io.nessus.actions.maven.main;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.net.ssl.SSLContext;

import io.nessus.actions.core.NessusConfig;
import io.nessus.actions.core.service.KeycloakService;
import io.nessus.actions.maven.MavenApplication;
import io.nessus.actions.maven.service.TaskExecutorService;
import io.nessus.common.main.AbstractMain;
import io.nessus.common.rest.JaxrsServer;
import io.nessus.common.rest.SSLContextBuilder;

public class MavenMain extends AbstractMain<NessusConfig, MavenOptions> {

    public static void main(String... args) throws Exception {

    	NessusConfig config = NessusConfig.createConfig();
    	
    	new MavenMain(config)
    		.start(args);
    }

    public MavenMain(NessusConfig config) throws IOException {
        super(config);
		config.addService(new KeycloakService(config));
		config.addService(new TaskExecutorService(config));
    }

    @Override
    protected MavenOptions createOptions() {
        return new MavenOptions();
    }

	@Override
    protected void doStart(MavenOptions options) throws Exception {
        
    	String mavenUrl = config.getMavenUrl();
    	String mavenTLSUrl = config.getMavenTLSUrl();
    	
    	boolean withTLS = isTLSEnabled();
    	
        logInfo("***************************************************");
        if (withTLS) logInfo("Starting {}", mavenTLSUrl);
        logInfo("Starting {}", mavenUrl);
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
		
		URL url = new URL(config.getMavenUrl());

		// Undertow needs to bind to 0.0.0.0 in Docker
		// At least for now

		String host = "0.0.0.0";
		int port = url.getPort();
		
		JaxrsServer server = new JaxrsServer(getConfig())
				.withHostname(host)
				.withHttpPort(port);
		
		if (withTLS) {
			
			String alias = "nessus-actions-maven";
			Path tlsCrt = Paths.get(config.getTLSCrt());
			Path tlsKey = Paths.get(config.getTLSKey());
			
			SSLContext sslContext = new SSLContextBuilder()
					.keystorePath(Paths.get("/tmp/keystore.jks"))
					.addCertificate(alias, tlsCrt)
					.addPrivateKey(alias, tlsKey)
					.build();
			
			SSLContext.setDefault(sslContext);
			
			URL tlsUrl = new URL(config.getMavenTLSUrl());
			int tlsPort = tlsUrl.getPort();
			
			server.withHttpsPort(tlsPort, sslContext);
		}
		
		server.deploy("/maven/api", new MavenApplication(getConfig()));
		
		return server;
	}
	
	private boolean isTLSEnabled() {
		if (!config.isUseTLS()) return false;
		String tlsUrl = config.getMavenTLSUrl();
		String tlsCert = config.getTLSCrt();
		String tlsKey = config.getTLSKey();
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

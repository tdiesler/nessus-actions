package io.nessus.actions.jaxrs.main;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.net.ssl.SSLContext;

import io.nessus.actions.jaxrs.JaxrsApplication;
import io.nessus.common.main.AbstractMain;
import io.nessus.common.rest.JaxrsServer;
import io.nessus.common.rest.SSLContextBuilder;

public class JaxrsMain extends AbstractMain<JaxrsConfig, JaxrsOptions> {

    public static void main(String... args) throws Exception {

    	JaxrsConfig config = JaxrsConfig.createConfig();
    	
    	new JaxrsMain(config)
    		.start(args);
    }

    public JaxrsMain(JaxrsConfig config) throws IOException {
        super(config);
    }

    @Override
    protected JaxrsOptions createOptions() {
        return new JaxrsOptions();
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
			
			String alias = "nessus-actions-jaxrs";
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
			
			server.withHttpsPort(tlsPort, sslContext);
		}
		
		server.deploy("/jaxrs/api", new JaxrsApplication(getConfig()));
		
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

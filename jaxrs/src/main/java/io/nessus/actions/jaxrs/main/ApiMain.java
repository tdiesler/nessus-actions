package io.nessus.actions.jaxrs.main;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.net.ssl.SSLContext;

import io.nessus.actions.jaxrs.ApiConfig;
import io.nessus.actions.jaxrs.ApiRoot;
import io.nessus.actions.jaxrs.JaxrsServer;
import io.nessus.actions.jaxrs.service.ApiService;
import io.nessus.actions.jaxrs.utils.SSLContextBuilder;
import io.nessus.common.main.AbstractMain;

public class ApiMain extends AbstractMain<ApiConfig, ApiOptions> {

    public static void main(String... args) throws Exception {

    	ApiConfig config = ApiConfig.createConfig();
    	
    	new ApiMain(config)
    		.start(args);
    }

    public ApiMain(ApiConfig config) throws IOException {
        super(config);
        config.addService(new ApiService(config));
    }

    @Override
    protected ApiOptions createOptions() {
        return new ApiOptions();
    }

	@Override
    protected void doStart(ApiOptions options) throws Exception {
        
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
		
		server.deployApplication(ApiRoot.class);
		
		return server;
	}
	
	private boolean isTLSEnabled() {
		Integer tlsPort = config.getPortalTLSPort();
		String tlsCert = config.getPortalTLSCrt();
		String tlsKey = config.getPortalTLSKey();
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
		logInfo("TLS Port: {}", tlsPort);
		logInfo("TLS Crt: {}", tlsCert);
		logInfo("TLS Key: {}", tlsKey);
		logInfo("TLS Enabled");
		return true;
	}
}

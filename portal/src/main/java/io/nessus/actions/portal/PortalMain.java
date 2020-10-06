package io.nessus.actions.portal;

import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.util.PortProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nessus.actions.portal.resources.PortalApi;

public class PortalMain {

	static final Logger LOG = LoggerFactory.getLogger(PortalMain.class);

	public static void main(String[] args) {
		
		String hostname = PortProvider.getHost();
		int port = PortProvider.getPort();
		
		UndertowJaxrsServer server = new UndertowJaxrsServer()
				.setHostname(hostname)
				.setPort(port);
		
		server.deployOldStyle(PortalApi.class);
        server.start();
        
        LOG.info("Portal API: http://{}:{}/portal/api", hostname, port);
	}
}

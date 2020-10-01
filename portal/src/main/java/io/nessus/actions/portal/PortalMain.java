package io.nessus.actions.portal;

import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nessus.actions.portal.resources.ApiRoot;

public class PortalMain {

	static final Logger LOG = LoggerFactory.getLogger(PortalMain.class);

	public static void main(String[] args) {
		
		UndertowJaxrsServer server = new UndertowJaxrsServer()
				.setPort(8080);

		server.deployOldStyle(ApiRoot.class);
        server.start();
	}
}

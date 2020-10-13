package io.nessus.actions.portal;

import javax.ws.rs.Path;

import io.nessus.actions.portal.main.PortalConfig;

@Path("/portal")
public class WebRoot extends AbstractWebResource  {

	public WebRoot(PortalConfig config) {
		super(config);
	}
}
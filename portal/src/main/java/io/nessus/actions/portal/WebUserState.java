package io.nessus.actions.portal;

import org.apache.velocity.VelocityContext;

import io.nessus.actions.jaxrs.type.UserState;
import io.nessus.actions.portal.main.PortalConfig;
import io.nessus.common.AssertState;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;

public class WebUserState extends AbstractUserResource {

	public WebUserState(PortalConfig config) {
		super(config);
	}

	@Override
	protected String handlePageRequest(HttpServerExchange exchange, VelocityContext context, Session session) throws Exception {
		
		UserState userStatus = getAttribute(session, UserState.class);
		AssertState.notNull(userStatus, "Null userStatus");
		
    	context.put("user", userStatus);
    	
        return "template/user-state.vm";
	}
}
package io.nessus.actions.portal;

import org.apache.velocity.VelocityContext;

import io.nessus.actions.jaxrs.type.UserState;
import io.nessus.common.AssertState;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;

public class WebUserHome extends AbstractUserResource {

	@Override
	protected String handlePageRequest(HttpServerExchange exchange, VelocityContext context, Session session) throws Exception {
		
		UserState userState = getAttribute(session, UserState.class);
		AssertState.notNull(userState, "Null userStatus");
		
    	context.put("user", userState);
    	
        return "template/user-home.vm";
	}
}
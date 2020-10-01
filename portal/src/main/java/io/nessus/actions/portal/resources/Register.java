package io.nessus.actions.portal.resources;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/register")
public class Register extends AbstractResource {
	
	private final Map<String, User> registeredUsers = new HashMap<>();
	
	@POST
	public Response post(@FormParam("email") String email, @FormParam("password") String password) {
		
		if (email == null || email.isEmpty()) {
			return Response.status(403).entity("Empty email").build();
		}
		
		if (password == null || password.isEmpty()) {
			return Response.status(403).entity("Empty password").build();
		}
		
		User was = registeredUsers.get(email);
		if (was != null) {
			return Response.status(403).entity("User already registered").build();
		}
		
		User user = new User(email, password);
		registeredUsers.put(email, user);
		LOG.info("Register: {}", user);
		
		String res = toJson(new MessageResponse("success"));
		return Response.ok(res).build();
	}

	boolean unregister(String email) {
		User user = registeredUsers.remove(email);
		return user != null;
	}
	
	Set<User> getRegisteredUsers() {
		Set<User> users = new HashSet<>(registeredUsers.values());
		return Collections.unmodifiableSet(users);
	}

	static class User {
		
		final String email;
		final String password;
		
		User(String email, String password) {
			this.email = email;
			this.password = password;
		}
		
		public String toString() {
			return email;
		}
	}
}
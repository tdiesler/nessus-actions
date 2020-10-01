package io.nessus.actions.model.utils;

import java.util.Arrays;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;

/**
 * A simple username/password callback handler
 * 
 */
public class UsernamePasswordHandler implements CallbackHandler {

	private final String username;
	private final char[] password;
	
	public UsernamePasswordHandler(String username, char[] password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public void handle(Callback[] callbacks) {
		Arrays.asList(callbacks).stream().forEach(cb -> {
			if (cb instanceof NameCallback) {
				((NameCallback) cb).setName(username);
			}
			else if (cb instanceof PasswordCallback) {
				((PasswordCallback) cb).setPassword(password);
			}
		});
	}
}
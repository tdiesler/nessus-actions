package io.nessus.actions.portal.web;

import io.nessus.common.AssertArg;

public class UserSession {
	
	final String username;
	final String refreshToken;
	
	public UserSession(String username, String refreshToken) {
		AssertArg.notNull(refreshToken, "Null refreshToken");
		AssertArg.notNull(username, "Null username");
		this.refreshToken = refreshToken;
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public String getRefreshToken() {
		return refreshToken;
	}
}
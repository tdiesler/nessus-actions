package io.nessus.actions.portal.web;

import io.nessus.common.AssertArg;

public class UserStatus {
	
	final String username;
	
	public UserStatus(String username) {
		AssertArg.notNull(username, "Null username");
		this.username = username;
	}

	public String getUsername() {
		return username;
	}
}
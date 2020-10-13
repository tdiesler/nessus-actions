package io.nessus.actions.jaxrs.type;

import io.nessus.common.AssertArg;

public class UserModelBase {
	
	public final String userId;
	public final String title;
	public final String content;
	
	public UserModelBase(String userId, String title, String content) {
		AssertArg.notNull(userId, "Null userId");
		AssertArg.notNull(title, "Null title");
		this.userId = userId;
		this.title = title;
		this.content = content;
	}

	public String getUserId() {
		return userId;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}
}
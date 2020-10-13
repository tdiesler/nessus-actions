package io.nessus.actions.jaxrs.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserTokens {
	
	public final String userId;
	public final String accessToken;
	public final String refreshToken;
	
	@JsonCreator
	public UserTokens(
		@JsonProperty(value = "userId", required = true) String userId, 
		@JsonProperty(value = "accessToken", required = true) String accessToken, 
		@JsonProperty(value = "refreshToken", required = true) String refreshToken) {
		this.userId = userId;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	public UserTokens(UserTokens tokens, String accessToken) {
		this.userId = tokens.userId;
		this.accessToken = accessToken;
		this.refreshToken = tokens.refreshToken;
	}
}
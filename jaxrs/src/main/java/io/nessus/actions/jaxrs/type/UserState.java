package io.nessus.actions.jaxrs.type;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.nessus.actions.core.types.KeycloakUserInfo;

public class UserState extends UserBase {
	
	private final String userId;
	private final int loginCount;
	private final Date lastLogin;
	private final String status;
	
	@JsonCreator
	public UserState(
			@JsonProperty(value = "id", required = true) String userId, 
			@JsonProperty(value = "username", required = true) String username, 
			@JsonProperty(value = "firstName", required = true) String firstName, 
			@JsonProperty(value = "lastName", required = true) String lastName, 
			@JsonProperty(value = "email", required = true) String email,
			@JsonProperty(value = "logins", required = true) Integer loginCount,
			@JsonProperty(value = "lastLogin", required = true) Date lastLogin,
			@JsonProperty(value = "status", required = true) String status) {
			super(username, firstName, lastName, email);
			this.userId = userId;
			this.loginCount = loginCount;
			this.lastLogin = lastLogin;
			this.status = status;
		}
		
	public UserState(KeycloakUserInfo kcinfo, int loginCount, Date lastLogin, String status) {
			super(kcinfo.username, kcinfo.givenName, kcinfo.familyName, kcinfo.email);
			this.userId = kcinfo.subject;
			this.loginCount = loginCount;
			this.lastLogin = lastLogin;
			this.status = status;
		}
		
	@JsonProperty(value = "id", required = true)
	public String getUserId() {
		return userId;
	}

	@JsonProperty(value = "logins")
	public int getLoginCount() {
		return loginCount;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public String getStatus() {
		return status;
	}
}
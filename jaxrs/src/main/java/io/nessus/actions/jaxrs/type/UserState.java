package io.nessus.actions.jaxrs.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserState extends UserBase {
	
	private String id;
	private String status;
	private Boolean emailVerified;
	
	@JsonCreator
	public UserState(
		@JsonProperty(value = "username", required = true) String username, 
		@JsonProperty(value = "firstName", required = true) String firstName, 
		@JsonProperty(value = "lastName", required = true) String lastName, 
		@JsonProperty(value = "email", required = true) String email) {
		super(username, firstName, lastName, email);
	}
	
	public UserState(KeycloakUserInfo kcinfo) {
		super(kcinfo.username, kcinfo.givenName, kcinfo.familyName, kcinfo.email);
		this.id = kcinfo.subject;
		this.emailVerified = kcinfo.emailVerified;
		this.status = "Active";
	}

	@JsonProperty(value = "id", required = true)
	public String getId() {
		return id;
	}

	@JsonProperty(value = "emailVerified", required = true)
	public Boolean isEmailVerified() {
		return emailVerified;
	}

	@JsonProperty(value = "status", required = true)
	public String getStatus() {
		return status;
	}
}
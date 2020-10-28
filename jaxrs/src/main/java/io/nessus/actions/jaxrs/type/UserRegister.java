package io.nessus.actions.jaxrs.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.nessus.actions.core.types.KeycloakUserRegister;

@JsonInclude(Include.NON_EMPTY)
public class UserRegister extends UserBase {
	
	private final String password;
	
	@JsonCreator
	public UserRegister(
		@JsonProperty(value = "username", required = true) String username, 
		@JsonProperty(value = "firstName", required = true) String firstName, 
		@JsonProperty(value = "lastName", required = true) String lastName, 
		@JsonProperty(value = "email", required = true) String email,
		@JsonProperty(value = "password", required = true) String password) {
		super(username, firstName, lastName, email);
		this.password = password;
	}

	public String getPassword() {
		return password;
	}
	
	public KeycloakUserRegister toKeycloakUserRegister() {
		return new KeycloakUserRegister(firstName, lastName, email, username, password);
	}
}
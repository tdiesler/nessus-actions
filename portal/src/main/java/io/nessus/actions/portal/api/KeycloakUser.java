package io.nessus.actions.portal.api;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class KeycloakUser {
	
	final String username;
	final String firstName;
	final String lastName;
	final String email;
	final List<KeycloakCredentials> credentials;
	final Boolean enabled;
	
	@JsonCreator
	KeycloakUser(@JsonProperty("username") String username, 
		@JsonProperty("firstName") String firstName, 
		@JsonProperty("lastName") String lastName, 
		@JsonProperty("email") String email, 
		@JsonProperty("credentials") List<KeycloakCredentials> credentials,
		@JsonProperty("enabled") Boolean enabled) {
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.credentials = credentials;
		this.enabled = enabled;
	}

	public KeycloakUser(User user) {
		this.username = user.username;
		this.firstName = user.firstName;
		this.lastName = user.lastName;
		this.email = user.email;
		this.credentials = Arrays.asList(new KeycloakCredentials("password", user.password, false));
		this.enabled = true;
	}

	public String getUsername() {
		return username;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public List<KeycloakCredentials> getCredentials() {
		return credentials;
	}

	public Boolean getEnabled() {
		return enabled;
	}
}
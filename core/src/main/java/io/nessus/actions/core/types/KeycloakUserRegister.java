package io.nessus.actions.core.types;

import java.util.Arrays;
import java.util.List;

public class KeycloakUserRegister {
	
	private String username;
	private String firstName;
	private String lastName;
	private String email;
	private List<KeycloakCredentials> credentials;
	private Boolean enabled;
	
	public KeycloakUserRegister(String firstName, String lastName, String email, String username, String password) {
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.credentials = Arrays.asList(new KeycloakCredentials("password", password, false));
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
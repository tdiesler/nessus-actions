package io.nessus.actions.jaxrs.type;

import java.util.Arrays;
import java.util.List;

public class KeycloakUserRegister {
	
	private String username;
	private String firstName;
	private String lastName;
	private String email;
	private List<KeycloakCredentials> credentials;
	private Boolean enabled;
	
	public KeycloakUserRegister(UserRegister user) {
		this.username = user.getUsername();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.email = user.getEmail();
		this.credentials = Arrays.asList(new KeycloakCredentials("password", user.getPassword(), false));
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
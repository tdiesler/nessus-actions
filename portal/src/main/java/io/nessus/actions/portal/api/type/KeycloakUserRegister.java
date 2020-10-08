package io.nessus.actions.portal.api.type;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class KeycloakUserRegister {
	
	private String username;
	private String firstName;
	private String lastName;
	private String email;
	private List<KeycloakCredentials> credentials;
	private Boolean enabled;
	
	public KeycloakUserRegister() {
	}

	public KeycloakUserRegister(User user) {
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

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<KeycloakCredentials> getCredentials() {
		return credentials;
	}

	public void setCredentials(List<KeycloakCredentials> credentials) {
		this.credentials = credentials;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

}
package io.nessus.actions.portal.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
	
	final String username;
	final String firstName;
	final String lastName;
	final String email;
	final String password;
	
	@JsonCreator
	public User(
		@JsonProperty("firstName") String firstName, 
		@JsonProperty("lastName") String lastName, 
		@JsonProperty("email") String email, 
		@JsonProperty("username") String username, 
		@JsonProperty("password") String password) {
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
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

	public String getPassword() {
		return password;
	}
}
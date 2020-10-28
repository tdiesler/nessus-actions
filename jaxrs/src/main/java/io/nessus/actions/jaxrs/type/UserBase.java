package io.nessus.actions.jaxrs.type;

public class UserBase {
	
	protected final String username;
	protected final String firstName;
	protected final String lastName;
	protected final String email;
	
	UserBase(String username, String firstName, String lastName, String email) {
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
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
}
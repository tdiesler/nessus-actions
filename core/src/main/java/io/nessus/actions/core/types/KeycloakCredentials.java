package io.nessus.actions.core.types;

public class KeycloakCredentials {
	
	public String type;
	public String value;
	public boolean temporary;
	
	public KeycloakCredentials() {
	}
	
	public KeycloakCredentials(String type, String value, boolean temporary) {
		this.type = type;
		this.value = value;
		this.temporary = temporary;
	}
}
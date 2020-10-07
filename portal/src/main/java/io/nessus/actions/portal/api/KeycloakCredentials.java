package io.nessus.actions.portal.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class KeycloakCredentials {
	
	final String type;
	final String value;
	final boolean temporary;
	
	@JsonCreator
	KeycloakCredentials(
		@JsonProperty("type") String type, 
		@JsonProperty("value") String value, 
		@JsonProperty("temporary") boolean temporary) {
		this.type = type;
		this.value = value;
		this.temporary = temporary;
	}

	public String getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public boolean isTemporary() {
		return temporary;
	}
}
package io.nessus.actions.jaxrs.type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class KeycloakUserInfo {
	
	@JsonProperty("sub")
	public String subject;
	
	@JsonProperty("email_verified")
	public boolean emailVerified;

	public String name;
	public String username;
	public String email;
}
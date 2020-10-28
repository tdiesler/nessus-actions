package io.nessus.actions.core.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class KeycloakUserInfo {
	
	@JsonProperty("sub")
	public String subject;
	
	@JsonProperty("preferred_username")
	public String username;
	
	@JsonProperty("given_name")
	public String givenName;
	
	@JsonProperty("family_name")
	public String familyName;

	@JsonProperty("email")
	public String email;
	
	@JsonProperty("email_verified")
	public boolean emailVerified;

}
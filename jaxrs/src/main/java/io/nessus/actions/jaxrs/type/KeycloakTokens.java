package io.nessus.actions.jaxrs.type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class KeycloakTokens {

	@JsonProperty("access_token")
	public String accessToken;
	
	@JsonProperty("refresh_token")
	public String refreshToken;
}
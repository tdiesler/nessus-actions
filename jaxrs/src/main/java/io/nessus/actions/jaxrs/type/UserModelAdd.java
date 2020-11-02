package io.nessus.actions.jaxrs.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
public class UserModelAdd extends UserModelBase {
	
	@JsonCreator
	public UserModelAdd(
		@JsonProperty(value = "userId", required = true) String userId, 
		@JsonProperty(value = "content", required = true) String content) {
		super(userId, content);
	}
}
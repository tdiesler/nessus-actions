package io.nessus.actions.jaxrs.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserModel extends UserModelBase {
	
	public String modelId;
	
	@JsonCreator
	public UserModel(
		@JsonProperty(value = "modelId", required = true) String modelId, 
		@JsonProperty(value = "userId", required = true)  String userId, 
		@JsonProperty(value = "title", required = true) String title, 
		@JsonProperty(value = "content") String content) {
		super(userId, title, content);
		this.modelId = modelId;
	}

	public UserModel(UserModelAdd mod) {
		super(mod.userId, mod.title, mod.content);
	}
	
	public UserModel(UserModel mod) {
		super(mod.userId, mod.title, mod.content);
		this.modelId = mod.modelId;
	}
	
	public UserModel withModelId(String modelId) {
		return new UserModel(modelId, userId, title, content);
	}
	
	public UserModel withTitle(String title) {
		return new UserModel(modelId, userId, title, content);
	}
	
	public UserModel withContent(String content) {
		return new UserModel(modelId, userId, title, content);
	}

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}
}
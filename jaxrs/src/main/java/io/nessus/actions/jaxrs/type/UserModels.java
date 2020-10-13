package io.nessus.actions.jaxrs.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
public class UserModels {
	
	public final String userId;
	public final List<UserModel> models = new ArrayList<>();
	
	@JsonCreator
	public UserModels(
		@JsonProperty(value = "userId", required = true) String userId, 
		@JsonProperty(value = "models") List<UserModel> models) {
		this.userId = userId;
		if (models != null)
			this.models.addAll(models);
	}

	@JsonIgnore
	public boolean isEmpty() {
		return models.isEmpty();
	}
	
	@JsonIgnore
	public int size() {
		return models.size();
	}
	
	public String getUserId() {
		return userId;
	}

	public List<UserModel> getModels() {
		return Collections.unmodifiableList(models);
	}
}
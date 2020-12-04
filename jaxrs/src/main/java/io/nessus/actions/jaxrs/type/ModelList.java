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
public class ModelList {
	
	public enum ModelRuntime {
		standalone, docker, kubernetes, eap
	}
	
	private final String userId;
	private final List<Model> models = new ArrayList<>();
	
	@JsonCreator
	public ModelList(
		@JsonProperty(value = "userId", required = true) String userId, 
		@JsonProperty(value = "models") List<Model> models) {
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

	public List<Model> getModels() {
		return Collections.unmodifiableList(models);
	}
}
package io.nessus.actions.maven;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.nessus.common.CheckedExceptionWrapper;

@JsonInclude(Include.NON_EMPTY)
public class MavenBuildHandle {
	
	private final URI handle;
	
	@JsonCreator
	public MavenBuildHandle(
		@JsonProperty(value = "handle", required = true) URI handle) {
		this.handle = handle;
	}

	public URI getHandle() {
		return handle;
	}
	
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException ex) {
			throw CheckedExceptionWrapper.create(ex);
		}
	}
}
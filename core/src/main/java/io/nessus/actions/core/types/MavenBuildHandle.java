package io.nessus.actions.core.types;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MavenBuildHandle {
	
	public enum BuildStatus {
		Scheduled, Running, Success, Failure, Removed, NotFound
	}
	
	private final String id;
	private final URI location;
	private final BuildStatus status;
	
	@JsonCreator
	public MavenBuildHandle(
		@JsonProperty(value = "id", required = true) String id,
		@JsonProperty(value = "location", required = true) URI location,
		@JsonProperty(value = "status", required = true) BuildStatus status) {
		this.id = id;
		this.location = location;
		this.status = status;
	}
	
	public String getId() {
		return id;
	}

	public URI getLocation() {
		return location;
	}

	public BuildStatus getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return String.format("MavenBuildHandle[id=%s, loc=%s, status=%s]", id, location, status);
	}
}
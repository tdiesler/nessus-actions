package io.nessus.actions.core.types;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.nessus.common.AssertArg;

public class MavenBuildHandle {
	
	public enum BuildStatus {
		Scheduled, Running, Success, Failure, NotFound
	}
	
	private final String id;
	private final URI buildTarget;
	private final URI buildSources;
	private final BuildStatus status;
	
	@JsonCreator
	public MavenBuildHandle(
		@JsonProperty(value = "id", required = true) String id,
		@JsonProperty(value = "sources") URI sources,
		@JsonProperty(value = "target") URI target,
		@JsonProperty(value = "status", required = true) BuildStatus status) {
		AssertArg.notNull(id, "Null id");
		AssertArg.notNull(status, "Null status");
		this.id = id;
		this.buildSources = sources;
		this.buildTarget = target;
		this.status = status;
	}
	
	public String getId() {
		return id;
	}

	@JsonProperty(value = "sources")
	public URI getBuildSources() {
		return buildSources;
	}

	@JsonProperty(value = "target")
	public URI getBuildTarget() {
		return buildTarget;
	}

	@JsonProperty(value = "status")
	public BuildStatus getBuildStatus() {
		return status;
	}

	@Override
	public String toString() {
		return String.format("MavenBuildHandle[id=%s, sources=%s, target=%s, status=%s]", id, buildSources, buildTarget, status);
	}
}
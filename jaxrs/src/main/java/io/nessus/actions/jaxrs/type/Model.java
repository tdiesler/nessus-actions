package io.nessus.actions.jaxrs.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.nessus.actions.core.types.MavenBuildHandle.BuildStatus;

@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Model extends ModelBase {
	
	public enum TargetRuntime {
		
		standalone, docker, javaee;
		
		public String display() {
			String tostr = toString();
			char ch = tostr.charAt(0);
			String display = Character.toUpperCase(ch) + tostr.substring(1);
			if (this == javaee) display = "JavaEE"; 
			return display;
		}
	}
	
	private final String modelId;
	private final List<ModelState> states = new ArrayList<>();
	
	@JsonCreator
	public Model(
		@JsonProperty(value = "modelId", required = true) String modelId, 
		@JsonProperty(value = "userId", required = true)  String userId, 
		@JsonProperty(value = "content") String content,
		@JsonProperty(value = "states") List<ModelState> states) {
		super(userId, content);
		this.modelId = modelId;
		if (states != null)
			this.states.addAll(states);
	}

	public Model(ModelAdd mod) {
		this(null, mod.userId, mod.content, null);
	}
	
	public Model(Model mod) {
		super(mod.userId, mod.content);
		this.modelId = mod.modelId;
	}
	
	public Model withModelId(String modelId) {
		return new Model(modelId, userId, content, states);
	}
	
	public Model withContent(String content) {
		return new Model(modelId, userId, content, states);
	}

	public String getModelId() {
		return modelId;
	}

	@JsonProperty(value = "states")
	public List<ModelState> getModelStates() {
		return Collections.unmodifiableList(states);
	}

	public ModelState getModelState(TargetRuntime runtime) {
		ModelState state = states.stream().filter(ms -> ms.runtime == runtime)
				.findAny().orElse(new ModelState(runtime, BuildStatus.NotFound));
		return state;
	}

	@Override
	public String toString() {
		StringBuffer strbuf = new StringBuffer("Model " + modelId);
		states.forEach(ms -> strbuf.append("\n  " + ms));
		return strbuf.toString();
	}
	
	public static class ModelState {
		
		private final TargetRuntime runtime;
		private final BuildStatus status;
		
		@JsonCreator
		public ModelState(
			@JsonProperty(value = "runtime", required = true) TargetRuntime runtime, 
			@JsonProperty(value = "status", required = true) BuildStatus status) {
			this.runtime = runtime;
			this.status = status;
		}

		@JsonProperty(value = "runtime")
		public TargetRuntime getRuntime() {
			return runtime;
		}

		@JsonProperty(value = "status")
		public BuildStatus getBuildStatus() {
			return status;
		}

		@JsonIgnore
		public boolean isBusy() {
			return status == BuildStatus.Scheduled || status == BuildStatus.Running; 
		}
		
		@Override
		public String toString() {
			return String.format("%s: %s", runtime, status);
		}
	}
}
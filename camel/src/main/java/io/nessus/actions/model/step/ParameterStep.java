package io.nessus.actions.model.step;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import io.nessus.actions.model.utils.Parameters;

public interface ParameterStep<S extends Step> extends Step {
	
	S withParams(String spec);
	
	S withParam(String key, Object value);
	
	Parameters getParameters();

	public static class ParameterStepContent {
		
		protected final Parameters params = new Parameters();

		void withParams(String spec) {
			params.putAll(Parameters.fromString(spec));
		}
		
		void withParam(String key, Object value) {
			params.put(key, value);
		}
		
		@JsonGetter("params")
		public Map<String, Object> getParameterMap() {
			return params.toMap();
		}

		@JsonSetter("params")
		public void setParameterMap(Map<String, Object> values) {
			params.clear();
			params.putAll(values);
		}

		@JsonIgnore
		public Parameters getParameters() {
			return new Parameters(params);
		}
	}
}
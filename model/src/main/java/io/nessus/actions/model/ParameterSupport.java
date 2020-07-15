package io.nessus.actions.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ParameterSupport {
	
	protected final Parameters params = new Parameters();

	@JsonIgnore
	public Parameters getParameters() {
		return new Parameters(params);
	}
}
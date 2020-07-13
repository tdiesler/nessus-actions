package io.nessus.actions.model;

public class ParameterSupport {
	
	protected final Parameters params = new Parameters();

	public Parameters getParameters() {
		return new Parameters(params);
	}
}
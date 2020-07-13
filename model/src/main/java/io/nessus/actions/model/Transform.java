package io.nessus.actions.model;

import java.beans.ConstructorProperties;

public class Transform extends ParameterSupport {
	
	@ConstructorProperties({"format"})
	public Transform(String format) {
		params.put("format", format);
	}

	public String getFormat() {
		return params.get("format", String.class);
	}

	public Boolean isPretty() {
		return params.get("pretty", Boolean.FALSE);
	}

	public void setPretty(Boolean pretty) {
		params.put("pretty", pretty);
	}

}
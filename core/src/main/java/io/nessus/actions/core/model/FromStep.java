package io.nessus.actions.core.model;

import java.beans.ConstructorProperties;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import io.nessus.common.Parameters;

public class FromStep implements ComponentStep<FromStep> {

	final String comp;
	final String with;
	final Parameters params = new Parameters();
	
	@ConstructorProperties({"comp", "with"})
	public FromStep(String comp, String with) {
		this.comp = comp;
		this.with = with;
	}

	@Override
	public FromStep withParams(String spec) {
		params.putAll(Parameters.fromString(spec));
		return this;
	}

	@Override
	public FromStep withParam(String key, Object value) {
		params.put(key, value);
		return this;
	}

	@Override
	public String getComp() {
		return comp;
	}

	@Override
	public String getWith() {
		return with;
	}

	@JsonGetter("params")
	public Map<String, Object> getParametersMap() {
		return params.toMap();
	}

	@JsonSetter("params")
	public void setParameterMap(Map<String, Object> values) {
		params.clear();
		params.putAll(values);
	}

	@Override
	@JsonIgnore
	public Parameters getParameters() {
		return params;
	}

	@Override
	public String toCamelUri() {
		String name = ComponentGAV.fromString(comp).getName();
		String uri = String.format("%s:%s", name, with);
		int index = 0;
		for (Entry<String, Object> en : params.toMap().entrySet()) {
			uri += (++index == 1 ? "?" : "&");
			uri += en.getKey() + "=" + en.getValue();
		}
		return uri;
	}
}
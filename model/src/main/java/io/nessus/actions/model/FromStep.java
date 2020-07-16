package io.nessus.actions.model;

import java.beans.ConstructorProperties;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.nessus.actions.model.step.ComponentGAV;
import io.nessus.actions.model.step.ComponentStep;
import io.nessus.actions.model.utils.Parameters;

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
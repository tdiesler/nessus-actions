package io.nessus.actions.model;

import java.beans.ConstructorProperties;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Endpoint extends ParameterSupport {
	
	private String comp;
	private String with;
	
	@ConstructorProperties({"name"})
	public Endpoint(String name) {
		this.comp = name;
	}

	public Endpoint(String name, String with, Parameters params) {
		this.comp = name;
		this.with = with;
		this.params.putAll(params);
	}

	public String getName() {
		return comp;
	}

	public String getWith() {
		return with;
	}

	public void setWith(String with) {
		this.with = with;
	}

	@JsonGetter("params")
	public Map<String, Object> getParameterMap() {
		return params.toMap();
	}

	@JsonSetter("params")
	public void setParameters(Map<String, Object> params) {
		this.params.clear();
		this.params.putAll(params);
	}
	
	public String toCamelUri() {
		String name = Component.fromString(comp).getName();
		String uri = String.format("%s:%s", name, with);
		int index = 0;
		for (Entry<String, Object> en : params.toMap().entrySet()) {
			uri += (++index == 1 ? "?" : "&");
			uri += en.getKey() + "=" + en.getValue();
		}
		return uri;
	}
}
package io.nessus.actions.model;

import java.beans.ConstructorProperties;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Endpoint {
	
	private String comp;
	private String with;
	private Map<String, String> params = new LinkedHashMap<>();
	
	@ConstructorProperties({"name"})
	public Endpoint(String name) {
		this.comp = name;
	}

	public Endpoint(String name, String with, Map<String, String> params) {
		this.comp = name;
		this.with = with;
		this.params = new LinkedHashMap<>(params);
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
	public Map<String, String> getParameters() {
		return Collections.unmodifiableMap(params);
	}

	@JsonSetter("params")
	public void setParameters(Map<String, String> params) {
		this.params = new LinkedHashMap<>(params);
	}
	
	public String toCamelUri() {
		String name = Component.fromString(comp).getName();
		String uri = String.format("%s:%s", name, with);
		int index = 0;
		for (Entry<String, String> en : params.entrySet()) {
			uri += (++index == 1 ? "?" : "&");
			uri += en.getKey() + "=" + en.getValue();
		}
		return uri;
	}
}
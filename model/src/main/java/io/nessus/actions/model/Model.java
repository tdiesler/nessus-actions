package io.nessus.actions.model;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class Model {
	
	private final String name;
	private String runtime;
	private Endpoint from;
	private Endpoint to;
	private Transform marshal;
	
	public static Model read(URL url) throws IOException {
		return read(url.openStream());
	}
	
	public static Model read(InputStream input) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		return mapper.readValue(input, Model.class);
	}
	
	@ConstructorProperties({"name"})
	public Model(String name) {
		this.name = name;
	}
	
	public Model(String name, String runtime, Endpoint from, Endpoint to, Transform marshal) {
		this.name = name;
		this.runtime = runtime;
		this.from = from;
		this.to = to;
		this.marshal = marshal;
	}

	public String getName() {
		return name;
	}
	
	public String getRuntime() {
		return runtime;
	}

	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}

	public Endpoint getFrom() {
		return from;
	}
	
	public void setFrom(Endpoint from) {
		this.from = from;
	}
	
	public Endpoint getTo() {
		return to;
	}
	
	public void setTo(Endpoint to) {
		this.to = to;
	}
	
	public Transform getMarshal() {
		return marshal;
	}

	public void setMarshal(Transform marshal) {
		this.marshal = marshal;
	}

	@Override
	public String toString() {
        try {
	        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
	        mapper.setSerializationInclusion(Include.NON_NULL);
	        mapper.setSerializationInclusion(Include.NON_EMPTY);
	        ObjectWriter writer = mapper.writer().forType(Model.class);
			return writer.writeValueAsString(this);
		} catch (JsonProcessingException ex) {
			throw CheckedExceptionWrapper.create(ex);
		}
	}

}
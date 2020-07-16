package io.nessus.actions.model;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.nessus.actions.model.step.Step;
import io.nessus.actions.model.step.StepDeserializer;
import io.nessus.actions.model.utils.AssertState;
import io.nessus.actions.model.utils.CheckedExceptionWrapper;

@JsonPropertyOrder({"name", "runtime", "from" }) 
public class Model {
	
	public static final String CAMEL_ACTIONS_RESOURCE_NAME = "camel-actions.yaml";
	
	public enum TargetRuntime {
		eap, wildfly, docker, standalone;

		public boolean isWildFly() {
			return this == eap || this == wildfly;
		}
	}
	
	private final String name;
	private final TargetRuntime runtime;
	private final List<Step> steps = new ArrayList<>();
	
	private FromStep fromStep;
	
	@ConstructorProperties({"name", "runtime"})
	public Model(String name, TargetRuntime runtime) {
		this.name = name;
		this.runtime = runtime;
	}

	public static Model read(URL url) throws IOException {
		return read(url.openStream());
	}
	
	public static Model read(InputStream input) throws IOException {
        ObjectMapper mapper = createObjectMapper();    		
		return mapper.readValue(input, Model.class);
	}
	
	public static Model read(String content) throws IOException {
        ObjectMapper mapper = createObjectMapper();    		
    	return mapper.readValue(content, Model.class);
	}

	public Model withStep(Step step) {
		if (fromStep == null) {
			AssertState.isTrue(step instanceof FromStep, "Invalid first step: " + step);
			fromStep = (FromStep) step;
		} else {
			steps.add(step);
		}
		return this;
	}

	public String getName() {
		return name;
	}

	public TargetRuntime getRuntime() {
		return runtime;
	}

	@JsonGetter("from")
	public FromStep getFrom() {
		return fromStep;
	}

	@JsonSetter("from")
	void setFrom(FromStep from) {
		this.fromStep = from;
	}

	public List<Step> getSteps() {
		return steps;
	}

	private static ObjectMapper createObjectMapper() {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        SimpleModule module = new SimpleModule();
    	module.addDeserializer(Step.class, new StepDeserializer(null));
    	mapper.registerModule(module);
		return mapper;
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
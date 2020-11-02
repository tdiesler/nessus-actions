package io.nessus.actions.core.model;

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

import io.nessus.common.AssertState;
import io.nessus.common.CheckedExceptionWrapper;

@JsonPropertyOrder({"title", "runtime", "from" }) 
public class RouteModel {
	
	public static final String CAMEL_ROUTE_MODEL_RESOURCE_NAME = "camel-route-model.yaml";
	
	private final String title;
	private final List<Step> steps = new ArrayList<>();
	
	private FromStep fromStep;
	
	@ConstructorProperties({"title", "runtime"})
	public RouteModel(String title) {
		this.title = title;
	}

	public static RouteModel read(URL url) throws IOException {
		return read(url.openStream());
	}
	
	public static RouteModel read(InputStream input) throws IOException {
        ObjectMapper mapper = createObjectMapper();    		
		return mapper.readValue(input, RouteModel.class);
	}
	
	public static RouteModel read(String content) {
        ObjectMapper mapper = createObjectMapper();    		
    	try {
			return mapper.readValue(content, RouteModel.class);
		} catch (JsonProcessingException ex) {
			throw CheckedExceptionWrapper.create(ex);
		}
	}

	public RouteModel withTitle(String title) {
		RouteModel other = new RouteModel(title);
		other.withStep(fromStep);
		steps.forEach(st -> other.withStep(st));
		return other;
	}

	public RouteModel withStep(Step step) {
		if (fromStep == null) {
			AssertState.isTrue(step instanceof FromStep, "Invalid first step: " + step);
			fromStep = (FromStep) step;
		} else {
			steps.add(step);
		}
		return this;
	}

	public String getTitle() {
		return title;
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
	        ObjectWriter writer = mapper.writer().forType(RouteModel.class);
			return writer.writeValueAsString(this);
		} catch (JsonProcessingException ex) {
			throw CheckedExceptionWrapper.create(ex);
		}
	}
}
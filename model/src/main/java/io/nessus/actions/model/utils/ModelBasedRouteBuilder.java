package io.nessus.actions.model.utils;

import static io.nessus.actions.model.Model.CAMEL_ACTIONS_RESOURCE_NAME;

import java.io.IOException;
import java.net.URL;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spi.TypeConverterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nessus.actions.model.MarshalStep;
import io.nessus.actions.model.MarshalStep.MarshalStepContent;
import io.nessus.actions.model.Model;
import io.nessus.actions.model.ToStep;
import io.nessus.actions.model.UnmarshalStep;
import io.nessus.actions.model.UnmarshalStep.UnmarshalStepContent;

/**
 *  A generic model based route builder
 *  
 *  Including this class in your deployment does not implicitly 
 *  enable the Camel substem.
 *  
 *  To enable the Camel subsystem with this route builder, subclass
 *  and add ...
 *  
 *  @CamelAware
 *  @ApplicationScoped
 */
public abstract class ModelBasedRouteBuilder extends RouteBuilder {

	protected final Logger LOG = LoggerFactory.getLogger(getClass());
	
	@Override
	public void configure() throws Exception {

		Model model = loadModel();
		
		configureWithModel(model);
	}

	protected Model loadModel() throws IOException {
		// Find the Camel Actions definition resource
		
		String resource = CAMEL_ACTIONS_RESOURCE_NAME;
		URL input = getClass().getResource(resource);
		if (input == null && !resource.startsWith("/")) 
			input = getClass().getResource("/" + resource);
		
		AssertState.notNull(input, "Cannot find: " + resource);
		
		// Read the Camel Actions Model
		
		LOG.warn("Configure {} from {}", ModelBasedRouteBuilder.class.getName(), input);
		Model model = Model.read(input);
		return model;
	}

	protected void configureWithModel(Model model) {
		
		// [TODO] Remove when this is part of Camel
		// [CAMEL-15301] Provide various type converters for camel-xchange
		TypeConverterRegistry registry = getContext().getTypeConverterRegistry();
		registry.addTypeConverters(new TickerTypeConverters());
		
		// Define the Camel route using the Model
		
        RouteDefinition rdef = fromF(model.getFrom().toCamelUri());
        model.getSteps().forEach(step -> {
        	if (step instanceof ToStep) {
        		ToStep aux = (ToStep) step;
        		rdef.to(aux.toCamelUri());
        	}
        	else if (step instanceof MarshalStep) {
        		MarshalStep aux = (MarshalStep) step;
        		MarshalStepContent content = aux.getContent();
            	String format = content.getFormat();
            	if ("json".equals(format)) {
            		Boolean pretty = content.isPretty();
                    rdef.marshal().json(JsonLibrary.Jackson, pretty);
            	}
        	}
        	else if (step instanceof UnmarshalStep) {
        		UnmarshalStep aux = (UnmarshalStep) step;
            	UnmarshalStepContent content = aux.getContent();
            	String format = content.getFormat();
            	if ("json".equals(format)) {
                    rdef.unmarshal().json(JsonLibrary.Jackson);
            	}
        	}
        });
	}
}
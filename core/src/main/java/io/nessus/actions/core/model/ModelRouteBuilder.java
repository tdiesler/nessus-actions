package io.nessus.actions.core.model;

import static io.nessus.actions.core.model.RouteModel.CAMEL_ROUTE_MODEL_RESOURCE_NAME;

import java.io.IOException;
import java.net.URL;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spi.TypeConverterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nessus.actions.core.model.MarshalStep.MarshalStepContent;
import io.nessus.actions.core.model.UnmarshalStep.UnmarshalStepContent;
import io.nessus.common.AssertState;

/**
 *  A generic model based route builder
 */
public class ModelRouteBuilder extends RouteBuilder {

	static final Logger LOG = LoggerFactory.getLogger(ModelRouteBuilder.class);
	
	private String modelResource;
	
	public ModelRouteBuilder() {
		modelResource = "/" + CAMEL_ROUTE_MODEL_RESOURCE_NAME;
	}
	
	public ModelRouteBuilder withModelResource(String respath) {
		this.modelResource = respath;
		return this;
	}

	@Override
	public void configure() throws Exception {
		configure(this);
	}
	
	public void configure(RouteBuilder routes) throws Exception {
		configureWithModel(routes, loadModel());
	}

	public RouteModel loadModel() throws IOException {
		
		// Find the Camel Actions definition resource
		
		URL resurl = getClass().getResource(modelResource);
		AssertState.notNull(resurl, "Cannot find: " + modelResource);
		
		// Read the Camel Actions Model
		
		LOG.info("Loading model ...");
		
		RouteModel model = RouteModel.read(resurl);
		return model;
	}

	public void configureWithModel(RouteBuilder routes, RouteModel model) {
		
		LOG.info("Configure with {}", model);
		
		// Define the Camel route using the Model
		
        String fromUri = model.getFrom().toCamelUri();
        LOG.info("From: {}", fromUri);
        
		RouteDefinition rdef = routes.fromF(fromUri);
		
        model.getSteps().forEach(step -> {
        	
        	if (step instanceof ToStep) {
        		ToStep aux = (ToStep) step;
        		String toUri = aux.toCamelUri();
                LOG.info("To: {}", toUri);
				rdef.to(toUri);
        	}
        	
        	else if (step instanceof MarshalStep) {
        		
        		MarshalStep aux = (MarshalStep) step;
        		MarshalStepContent content = aux.getContent();
            	String format = content.getFormat();
                LOG.info("Marshal: {}", format);
            	if ("json".equals(format)) {
            		Boolean pretty = content.isPretty();
                    rdef.marshal().json(JsonLibrary.Jackson, pretty);
            	}
        	}
        	else if (step instanceof UnmarshalStep) {
        		UnmarshalStep aux = (UnmarshalStep) step;
            	UnmarshalStepContent content = aux.getContent();
            	String format = content.getFormat();
                LOG.info("Unmarshal: {}", format);
            	if ("json".equals(format)) {
                    rdef.unmarshal().json(JsonLibrary.Jackson);
            	}
        	}
        });
	}

	public void addToCamelContext(CamelContext context) throws Exception {
		
		// [TODO] Remove when this is part of Camel
		// [CAMEL-15301] Provide various type converters for camel-xchange
		
		TypeConverterRegistry registry = context.getTypeConverterRegistry();
		registry.addTypeConverters(new TickerTypeConverters());
	}
}
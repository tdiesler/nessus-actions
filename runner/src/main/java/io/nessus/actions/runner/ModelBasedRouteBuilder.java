package io.nessus.actions.runner;

import static io.nessus.actions.model.Model.CAMEL_ACTIONS_RESOURCE_NAME;

import java.net.URL;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spi.TypeConverterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nessus.actions.model.AssertState;
import io.nessus.actions.model.Model;
import io.nessus.actions.model.Transform;
import io.nessus.actions.model.converters.TickerTypeConverters;

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

		// Find the Camel Actions definition resource
		
		String resource = CAMEL_ACTIONS_RESOURCE_NAME;
		URL input = getClass().getResource(resource);
		if (input == null && !resource.startsWith("/")) 
			input = getClass().getResource("/" + resource);
		
		AssertState.notNull(input, "Cannot find: " + resource);
		
		// Read the Camel Actions Model
		
		LOG.warn("Configure {} from {}", ModelBasedRouteBuilder.class.getName(), input);
		Model model = Model.read(input);
		
		// [TODO] Remove when this is part of Camel
		// [CAMEL-15301] Provide various type converters for camel-xchange
		TypeConverterRegistry registry = getContext().getTypeConverterRegistry();
		registry.addTypeConverters(new TickerTypeConverters());
		
		// Define the Camel route using the Model
		
        RouteDefinition rdef = fromF(model.getFrom().toCamelUri())
            	.to(model.getTo().toCamelUri());
            
        Transform marshal = model.getMarshal();
		if (marshal != null) {
        	String format = marshal.getFormat();
        	if ("json".equals(format)) {
        		Boolean pretty = marshal.isPretty();
                rdef.marshal().json(JsonLibrary.Jackson, pretty);
        	}
        }
	}
}
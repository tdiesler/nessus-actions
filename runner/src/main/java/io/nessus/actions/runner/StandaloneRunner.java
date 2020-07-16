package io.nessus.actions.runner;

import java.io.IOException;

import org.apache.camel.CamelContext;
import org.apache.camel.TypeConverters;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.TypeConverterRegistry;

import io.nessus.actions.model.Model;
import io.nessus.actions.model.utils.CheckedExceptionWrapper;
import io.nessus.actions.model.utils.ModelBasedRouteBuilder;

public class StandaloneRunner implements AutoCloseable {

	private CamelContext camelctx;
	
	public StandaloneRunner(Model model) {
		try {
			create(model);
		} catch (Exception ex) {
			throw CheckedExceptionWrapper.create(ex);
		}
	}

	public void create(Model model) throws Exception {
		camelctx = new DefaultCamelContext();
    	camelctx.addRoutes(new ModelBasedRouteBuilder() {
			@Override
			protected Model loadModel() throws IOException {
				return model;
			}
    	});
	}

	public CamelContext getCamelContext() {
		return camelctx;
	}

	public void addTypeConverters(TypeConverters typeConverters) {
		TypeConverterRegistry registry = camelctx.getTypeConverterRegistry();
		registry.addTypeConverters(typeConverters);
	}

	public void start() {
		camelctx.start();
	}

	@Override
	public void close() throws Exception {
		camelctx.close();
	}
}
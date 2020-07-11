package io.nessus.actions.runner;

import org.apache.camel.CamelContext;
import org.apache.camel.TypeConverters;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.TypeConverterRegistry;

import io.nessus.actions.model.CheckedExceptionWrapper;
import io.nessus.actions.model.Model;

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
		camelctx.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                fromF(model.getFrom().toCamelUri())
                	.to(model.getTo().toCamelUri());
            }
        });
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
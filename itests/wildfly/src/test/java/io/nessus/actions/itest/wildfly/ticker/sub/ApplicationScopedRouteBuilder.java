package io.nessus.actions.itest.wildfly.ticker.sub;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spi.TypeConverterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wildfly.extension.camel.CamelAware;

@CamelAware
@ApplicationScoped
public class ApplicationScopedRouteBuilder extends RouteBuilder {

	static final Logger LOG = LoggerFactory.getLogger(ApplicationScopedRouteBuilder.class);
	
	@Override
	public void configure() throws Exception {
		
        String httpUrl = "http://127.0.0.1:8080/ticker";
		LOG.warn("Configure {} with {}", ApplicationScopedRouteBuilder.class.getSimpleName(), httpUrl);

		TypeConverterRegistry registry = getContext().getTypeConverterRegistry();
		registry.addTypeConverters(new TickerTypeConverters());
		
		fromF("undertow:" + httpUrl)
	    	.to("xchange:binance?service=marketdata&method=ticker")
	    	.marshal().json(JsonLibrary.Jackson, true);
		
	}
}
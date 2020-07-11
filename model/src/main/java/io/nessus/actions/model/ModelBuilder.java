package io.nessus.actions.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ModelBuilder {
	
	private final String name;
	private String runtime;
	private Endpoint from;
	private Endpoint to;
	
	public ModelBuilder(String name) {
		this.name = name;
	}

	public ModelBuilder runtime(String runtime) {
		this.runtime = runtime;
		return this;
	}

	public EndpointBuilder from(String name) {
		return new EndpointBuilder(name, ep -> {
			this.from = ep;
		});
	}

	public EndpointBuilder to(String name) {
		return new EndpointBuilder(name, ep -> {
			this.to = ep;
		});
	}

	public Model build() {
		Model model = new Model(name, runtime, from, to);
		return model;
	}

    public class EndpointBuilder {
    	
    	private final String name;
    	private final Consumer<Endpoint> consumer;
    	private final Map<String, String> params = new LinkedHashMap<>();
    	private String with;
    	
		public EndpointBuilder(String name, Consumer<Endpoint> consumer) {
			this.consumer = consumer;
			this.name = name;
		}

		public EndpointBuilder param(String key, String val) {
			params.put(key, val);
			return this;
		}

		public EndpointBuilder with(String with) {
			this.with = with;
			return this;
		}

		public ModelBuilder build() {
			consumer.accept(new Endpoint(name, with, params));
			return ModelBuilder.this;
		}
    } 
}
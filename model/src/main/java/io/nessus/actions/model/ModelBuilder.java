package io.nessus.actions.model;

import java.util.function.Consumer;
import io.nessus.actions.model.Model.Runtime;

public class ModelBuilder {
	
	private final String name;
	private Runtime runtime;
	private Endpoint from;
	private Endpoint to;
	private Transform marshal;
	
	public ModelBuilder(String name) {
		this.name = name;
	}

	public ModelBuilder runtime(Runtime runtime) {
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

	public ModelBuilder marshall(String format, boolean pretty) {
		this.marshal = new Transform(format);
		this.marshal.setPretty(pretty);
		return this;
	}
	
	public Model build() {
		Model model = new Model(name, runtime, from, to, marshal);
		return model;
	}

    public class EndpointBuilder {
    	
    	private final String name;
    	private final Consumer<Endpoint> consumer;
    	private final Parameters params = new Parameters();
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
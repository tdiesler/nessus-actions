package io.nessus.actions.model;

public class Model {
	
	private final Endpoint source;
	private final Endpoint dest;
	
	public Model(Endpoint source, Endpoint dest) {
		this.source = source;
		this.dest = dest;
	}
	
	public Endpoint getFromEndpoint() {
		return source;
	}

	public Endpoint getToEndpoint() {
		return dest;
	}

	public static class Builder {
		
    	private Endpoint source;
    	private Endpoint dest;
    	
		public Builder from(String comp, String uri) {
    		this.source = new Endpoint(Component.fromString(comp), uri);
    		return this;
    	}
    	
		public Builder to(String comp, String uri) {
    		this.dest = new Endpoint(Component.fromString(comp), uri);
    		return this;
    	}
    	
    	public Model build() {
    		return new Model(source, dest);
    	}
	}
}
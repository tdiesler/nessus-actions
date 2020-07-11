package io.nessus.actions.model;

public class Endpoint {
	
	private final Component comp;
	private final String uri;
	
	public Endpoint(Component comp, String uri) {
		this.comp = comp;
		this.uri = uri;
	}
	
	public static Endpoint fromString(String spec) {
		int idx = spec.indexOf(':');
		AssertState.isTrue(idx > 0, "Invalid spec: " + spec);
		String head = spec.substring(0, idx);
		String uri = spec.substring(idx + 1);
		Component comp = Component.fromString(head);
		return new Endpoint(comp, uri);
	}
	
	public String toCamelUri() {
		return String.format("%s:%s", comp.getName(), uri);
	}
	
	public String toString() {
		return String.format("%s:%s", comp, uri);
	}
}
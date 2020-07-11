package io.nessus.actions.model;

public class Component {
	
	private final String group;
	private final String name;
	private final String version;
	
	public Component(String group, String name, String version) {
		this.group = group;
		this.name = name;
		this.version = version;
	}
	
	public static Component fromString(String spec) {
		int idx = spec.indexOf('@');
		AssertState.isTrue(idx > 0, "Invalid spec: " + spec);
		String version = spec.substring(idx + 1);
		String[] toks = spec.substring(0, idx).split("/");
		AssertState.isEqual(2, toks.length, "Invalid spec: " + spec);
		return new Component(toks[0], toks[1], version);
	}
	
	public String getGroup() {
		return group;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String toString() {
		return String.format("%s/%s@%s", group, name, version);
	}
}
package io.nessus.actions.core;

public class NessusConfigHolder {

	private static NessusConfig config;

	public static NessusConfig getConfig() {
		return config;
	}

	static void setConfig(NessusConfig config) {
		NessusConfigHolder.config = config;
	}
}

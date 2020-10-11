package io.nessus.actions.jaxrs.utils;

import io.nessus.actions.jaxrs.ApiApplication;
import io.nessus.actions.jaxrs.JaxrsConfig;

public class KeycloakUtils {

	public static String keycloakUrl(String path) {
		return keycloakUrl(path, false);
	}

	public static String keycloakUrl(String path, boolean tls) {
		JaxrsConfig config = ApiApplication.getInstance().getConfig();
		String keycloakUrl = tls ? config.getKeycloakTLSUrl() : config.getKeycloakUrl();
		return keycloakUrl + path;
	}
	
	public static String keycloakRealmPath(String realm, String path) {
		return "/realms/" + realm + path;
	}

	public static String keycloakRealmTokenPath(String realm) {
		return keycloakRealmTokenPath(realm, "");
	}
	
	public static String keycloakRealmTokenPath(String realm, String path) {
		return "/realms/" + realm + "/protocol/openid-connect/token" + path;
	}
}
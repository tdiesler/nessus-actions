package io.nessus.actions.jaxrs.utils;

import io.nessus.actions.jaxrs.JaxrsApplication;
import io.nessus.actions.jaxrs.main.JaxrsConfig;
import io.nessus.common.AssertArg;

public class KeycloakUtils {

	public static String keycloakUrl(String path, boolean tls) {
		JaxrsConfig config = JaxrsApplication.getInstance().getConfig();
		String keycloakUrl = tls ? config.getKeycloakTLSUrl() : config.getKeycloakUrl();
		return keycloakUrl + path;
	}
	
	public static String keycloakRealmPath(String realm, String path) {
		AssertArg.notNull(realm, "Null realm");
		AssertArg.notNull(path, "Null path");
		return "/realms/" + realm + path;
	}

	public static String keycloakRealmTokenPath(String realm) {
		return keycloakRealmTokenPath(realm, "");
	}
	
	public static String keycloakRealmTokenPath(String realm, String path) {
		AssertArg.notNull(realm, "Null realm");
		AssertArg.notNull(path, "Null path");
		return "/realms/" + realm + "/protocol/openid-connect/token" + path;
	}
}
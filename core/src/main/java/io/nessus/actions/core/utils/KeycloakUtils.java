package io.nessus.actions.core.utils;

import io.nessus.common.AssertArg;

public class KeycloakUtils {

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
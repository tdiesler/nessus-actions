package io.nessus.actions.jaxrs.main;

import org.kohsuke.args4j.Option;

import io.nessus.common.main.AbstractOptions;

public class ApiOptions extends AbstractOptions {

    public ApiOptions() {
        super("tryit");
    }

    @Option(name = "--keycloakUrl", usage = "The Keycloak URL")
    public String keycloakUrl;

    @Option(name = "--keycloakUser", usage = "The Keycloak master username")
    public String keycloakUser;

    @Option(name = "--keycloakPassword", usage = "The Keycloak master password")
    public String keycloakPassword;
}

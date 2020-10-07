package io.nessus.actions.portal;

import org.kohsuke.args4j.Option;

import io.nessus.common.main.AbstractOptions;

public class PortalOptions extends AbstractOptions {

    PortalOptions() {
        super("tryit");
    }

    @Option(name = "--keycloakUrl", usage = "The Keycloak URL")
    public String keycloakUrl;

    @Option(name = "--keycloakUser", usage = "The Keycloak master username")
    public String keycloakUser;

    @Option(name = "--keycloakPassword", usage = "The Keycloak master password")
    public String keycloakPassword;
}

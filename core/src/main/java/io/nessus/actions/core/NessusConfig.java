package io.nessus.actions.core;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.nessus.common.BasicConfig;
import io.nessus.common.CheckedExceptionWrapper;

@JsonInclude(Include.NON_NULL)
public class NessusConfig extends BasicConfig {

	@JsonCreator
	public NessusConfig(Map<String, String> cfgmap) {
		super(cfgmap);
		NessusConfigHolder.setConfig(this);
	}
	
	public static NessusConfig createConfig() {
		
		NessusConfig config;
		try {
			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
			URL resUrl = NessusConfig.class.getResource("/nessus-config.yaml");
			config = mapper.readValue(resUrl, NessusConfig.class);
		} catch (Exception ex) {
			throw CheckedExceptionWrapper.create(ex);
		}
		
		// Prepare the config with overwrites
		
		config.prepare(new LinkedHashMap<String, String>());
		
		return config;
	}
	
	@Override
	public void prepare(Map<String, String> mapping) {
		
		mapping.put("keycloakUrl", "KEYCLOAK_URL");
		mapping.put("keycloakTLSUrl", "KEYCLOAK_TLS_URL");
		mapping.put("keycloakUser", "KEYCLOAK_USER");
		mapping.put("keycloakPassword", "KEYCLOAK_PASSWORD");
		mapping.put("keycloakRealm", "KEYCLOAK_REALM");
		mapping.put("keycloakClient", "KEYCLOAK_CLIENT");
		mapping.put("mavenUrl", "MAVEN_URL");
		mapping.put("mavenTLSUrl", "MAVEN_TLS_URL");
		mapping.put("mavenTLSCrt", "MAVEN_TLS_CRT");
		mapping.put("mavenTLSKey", "MAVEN_TLS_KEY");
		mapping.put("jaxrsUrl", "JAXRS_URL");
		mapping.put("jaxrsTLSUrl", "JAXRS_TLS_URL");
		mapping.put("jaxrsTLSCrt", "JAXRS_TLS_CRT");
		mapping.put("jaxrsTLSKey", "JAXRS_TLS_KEY");
		mapping.put("portalUrl", "PORTAL_URL");
		mapping.put("portalTLSUrl", "PORTAL_TLS_URL");
		mapping.put("portalTLSCrt", "PORTAL_TLS_CRT");
		mapping.put("portalTLSKey", "PORTAL_TLS_KEY");
		mapping.put("registryUrl", "REGISTRY_URL");
		mapping.put("registryTLSUrl", "REGISTRY_TLS_URL");

		super.prepare(mapping);
	}

	public String getTLSCrt() {
		return getParameter("tlsCrt", "/etc/x509/https/tls.crt");
	}

	public String getTLSKey() {
		return getParameter("tlsKey", "/etc/x509/https/tls.key");
	}

	public String getMavenUrl() {
		return getParameter("mavenUrl", String.class);
	}

	public String getMavenTLSUrl() {
		return getParameter("mavenTLSUrl", String.class);
	}

	public String getRegistryUrl() {
		return getParameter("registryUrl", String.class);
	}

	public String getRegistryTLSUrl() {
		return getParameter("registryTLSUrl", String.class);
	}

	public String getKeycloakUrl() {
		return getParameter("keycloakUrl", String.class);
	}

	public String getKeycloakTLSUrl() {
		return getParameter("keycloakTLSUrl", String.class);
	}

	public String getMasterUser() {
		return getParameter("keycloakUser", String.class);
	}

	public String getMasterPassword() {
		return getParameter("keycloakPassword", String.class);
	}

	public String getKeycloakRealmId() {
		return getParameter("keycloakRealm", String.class);
	}

	public String getKeycloakClientId() {
		return getParameter("keycloakClient", String.class);
	}

	@JsonIgnore
	public String getKeycloakRefreshToken() {
		return getParameter("masterRefreshToken", String.class);
	}

	public String getJaxrsUrl() {
		return getParameter("jaxrsUrl", String.class);
	}

	public String getJaxrsTLSUrl() {
		return getParameter("jaxrsTLSUrl", String.class);
	}

	public String getPortalUrl() {
		return getParameter("portalUrl", String.class);
	}

	public String getPortalTLSUrl() {
		return getParameter("portalTLSUrl", String.class);
	}

	@JsonIgnore
	public boolean isUseTLS() {
		return getParameter("useTLS", false);
	}

	public void setUseTLS(boolean useTLS) {
		putParameter("useTLS", useTLS);
	}

	public Path getWorkspace() {
		return getParameter("nessusWorkspace", Paths.get("/var/nessus/workspace"));
	}
}

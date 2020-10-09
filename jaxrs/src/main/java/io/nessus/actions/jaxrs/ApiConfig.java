package io.nessus.actions.jaxrs;

import java.net.URL;
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
public class ApiConfig extends BasicConfig {

	@JsonCreator
	ApiConfig(Map<String, String> cfgmap) {
		super(cfgmap);
	}
	
	public static ApiConfig createConfig() {
		
		ApiConfig config;
		try {
			
			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
			URL resUrl = ApiConfig.class.getResource("/jaxrs-config.yaml");
			config = mapper.readValue(resUrl, ApiConfig.class);
			
		} catch (Exception ex) {
			throw CheckedExceptionWrapper.create(ex);
		}
		
		// Prepare the config with overwrites
		
		config.prepare(new LinkedHashMap<String, String>());
		
		return config;
	}
	
	@Override
	public void prepare(Map<String, String> mapping) {
		
		mapping.put("jaxrsHost", "JAXRS_HOST");
		mapping.put("jaxrsPort", "JAXRS_PORT");
		mapping.put("jaxrsTLSPort", "JAXRS_TLS_PORT");
		mapping.put("jaxrsTLSCrt", "JAXRS_TLS_CRT");
		mapping.put("jaxrsTLSKey", "JAXRS_TLS_KEY");
		mapping.put("keycloakUrl", "KEYCLOAK_URL");
		mapping.put("keycloakUser", "KEYCLOAK_USER");
		mapping.put("keycloakPassword", "KEYCLOAK_PASSWORD");
		mapping.put("keycloakRealm", "KEYCLOAK_REALM");
		mapping.put("keycloakClient", "KEYCLOAK_CLIENT");

		super.prepare(mapping);
	}

	public String getPortalHost() {
		return getParameter("jaxrsHost", String.class);
	}

	public Integer getPortalPort() {
		return getParameter("jaxrsPort", Integer.class);
	}

	public String getPortalUrl() {
		String host = getPortalHost();
		int port = getPortalPort();
		return String.format("http://%s:%d/tryit", host, port);
	}

	public Integer getPortalTLSPort() {
		return getParameter("jaxrsTLSPort", Integer.class);
	}

	public String getPortalTLSUrl() {
		String host = getPortalHost();
		Integer port = getPortalTLSPort();
		return String.format("https://%s:%d/tryit", host, port);
	}

	public String getPortalTLSCrt() {
		return getParameter("jaxrsTLSCrt", String.class);
	}

	public String getPortalTLSKey() {
		return getParameter("jaxrsTLSKey", String.class);
	}

	public String getKeycloakUrl() {
		return getParameter("keycloakUrl", String.class);
	}

	public String getMasterUsername() {
		return getParameter("keycloakUser", String.class);
	}

	public String getMasterPassword() {
		return getParameter("keycloakPassword", String.class);
	}

	public String getRealmId() {
		return getParameter("keycloakRealm", String.class);
	}

	public String getClientId() {
		return getParameter("keycloakClient", String.class);
	}

	@JsonIgnore
	public String getMasterRefreshToken() {
		return getParameter("masterRefreshToken", String.class);
	}
}

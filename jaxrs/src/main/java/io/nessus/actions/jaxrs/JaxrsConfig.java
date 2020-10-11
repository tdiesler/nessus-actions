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
public class JaxrsConfig extends BasicConfig {

	@JsonCreator
	JaxrsConfig(Map<String, String> cfgmap) {
		super(cfgmap);
	}
	
	public static JaxrsConfig createConfig() {
		
		JaxrsConfig config;
		try {
			
			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
			URL resUrl = JaxrsConfig.class.getResource("/jaxrs-config.yaml");
			config = mapper.readValue(resUrl, JaxrsConfig.class);
			
		} catch (Exception ex) {
			throw CheckedExceptionWrapper.create(ex);
		}
		
		// Prepare the config with overwrites
		
		config.prepare(new LinkedHashMap<String, String>());
		
		return config;
	}
	
	@Override
	public void prepare(Map<String, String> mapping) {
		
		mapping.put("jaxrsUrl", "JAXRS_URL");
		mapping.put("jaxrsTLSUrl", "JAXRS_TLS_URL");
		mapping.put("jaxrsTLSCrt", "JAXRS_TLS_CRT");
		mapping.put("jaxrsTLSKey", "JAXRS_TLS_KEY");
		mapping.put("keycloakUrl", "KEYCLOAK_URL");
		mapping.put("keycloakTLSUrl", "KEYCLOAK_TLS_URL");
		mapping.put("keycloakUser", "KEYCLOAK_USER");
		mapping.put("keycloakPassword", "KEYCLOAK_PASSWORD");
		mapping.put("keycloakRealm", "KEYCLOAK_REALM");
		mapping.put("keycloakClient", "KEYCLOAK_CLIENT");

		super.prepare(mapping);
	}

	public String getJaxrsUrl() {
		return getParameter("jaxrsUrl", String.class);
	}

	public String getJaxrsTLSUrl() {
		return getParameter("jaxrsTLSUrl", String.class);
	}

	public String getJaxrsTLSCrt() {
		return getParameter("jaxrsTLSCrt", String.class);
	}

	public String getJaxrsTLSKey() {
		return getParameter("jaxrsTLSKey", String.class);
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

	@JsonIgnore
	public boolean isUseTLS() {
		return getParameter("useTLS", false);
	}

	public void setUseTLS(boolean useTLS) {
		putParameter("useTLS", useTLS);
	}
}

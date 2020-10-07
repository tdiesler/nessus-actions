package io.nessus.actions.portal;

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
public class PortalConfig extends BasicConfig {

	@JsonCreator
	PortalConfig(Map<String, String> cfgmap) {
		super(cfgmap);
	}
	
	public static PortalConfig createConfig() {
		
		PortalConfig config;
		try {
			
			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
			URL resUrl = PortalConfig.class.getResource("/portal-config.yaml");
			config = mapper.readValue(resUrl, PortalConfig.class);
			
		} catch (Exception ex) {
			throw CheckedExceptionWrapper.create(ex);
		}
		
		// Prepare the config with overwrites
		
		config.prepare(new LinkedHashMap<String, String>());
		
		return config;
	}
	
	@Override
	public void prepare(Map<String, String> mapping) {
		
		mapping.put("portalUrl", "PORTAL_URL");
		mapping.put("keycloakUrl", "KEYCLOAK_URL");
		mapping.put("keycloakUser", "KEYCLOAK_USER");
		mapping.put("keycloakPassword", "KEYCLOAK_PASSWORD");
		mapping.put("keycloakRealm", "KEYCLOAK_REALM");
		mapping.put("keycloakClient", "KEYCLOAK_CLIENT");

		super.prepare(mapping);
	}

	public String getPortalUrl() {
		return getParameter("portalUrl", String.class);
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

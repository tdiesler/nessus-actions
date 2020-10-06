package io.nessus.actions.portal;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
		
		Map<String, String> mapping = new HashMap<>();
		mapping.put("realmId", "KEYCLOAK_REALM_ID");
		mapping.put("clientId", "KEYCLOAK_CLIENT_ID");
		mapping.put("keycloakUrl", "KEYCLOAK_BASE_URL");
		
		// Provide the combination of master username/password 
		// Note, this gives the service unlimited master access
		
		mapping.put("masterUsername", "KEYCLOAK_USERNAME");
		mapping.put("masterPassword", "KEYCLOAK_PASSWORD");
		
		PortalConfig config;
		try {
			
			// Initialize default properties from Json
			
			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
			URL resUrl = PortalConfig.class.getResource("/config.yaml");
			config = mapper.readValue(resUrl, PortalConfig.class);
			
			// Override with env vars
			
			for (Entry<String, String> en : mapping.entrySet()) {
				String value = System.getenv(en.getValue());
				if (value != null) {
					config.putParameter(en.getKey(), value);
				}
			}
			
			// Override with system properties
			
			for (Entry<String, String> en : mapping.entrySet()) {
				String value = System.getProperty(en.getKey());
				if (value != null) {
					config.putParameter(en.getKey(), value);
				}
			}
			
		} catch (Exception ex) {
			throw CheckedExceptionWrapper.create(ex);
		}
		
		return config;
	}

	public String getPortalUrl() {
		return getParameter("portalUrl", String.class);
	}

	public String getRealmId() {
		return getParameter("realmId", String.class);
	}

	public String getClientId() {
		return getParameter("clientId", String.class);
	}

	public String getKeycloakUrl() {
		return getParameter("keycloakUrl", String.class);
	}

	public String getMasterUsername() {
		return getParameter("masterUsername", String.class);
	}

	public String getMasterPassword() {
		return getParameter("masterPassword", String.class);
	}

	@JsonIgnore
	public String getMasterRefreshToken() {
		return getParameter("masterRefreshToken", String.class);
	}
}

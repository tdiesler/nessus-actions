package io.nessus.actions.portal.main;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.nessus.actions.jaxrs.main.JaxrsConfig;
import io.nessus.common.CheckedExceptionWrapper;

@JsonInclude(Include.NON_NULL)
public class PortalConfig extends JaxrsConfig {

	@JsonCreator
	public PortalConfig(Map<String, String> cfgmap) {
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
		mapping.put("portalTLSUrl", "PORTAL_TLS_URL");
		mapping.put("portalTLSCrt", "PORTAL_TLS_CRT");
		mapping.put("portalTLSKey", "PORTAL_TLS_KEY");

		super.prepare(mapping);
	}

	public String getPortalUrl() {
		return getParameter("portalUrl", String.class);
	}

	public String getPortalTLSUrl() {
		return getParameter("portalTLSUrl", String.class);
	}

	public String getPortalTLSCrt() {
		return getParameter("portalTLSCrt", String.class);
	}

	public String getPortalTLSKey() {
		return getParameter("portalTLSKey", String.class);
	}
}

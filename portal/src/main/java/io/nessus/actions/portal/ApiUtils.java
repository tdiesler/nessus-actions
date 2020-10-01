package io.nessus.actions.portal;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.test.TestPortProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class ApiUtils {
	
	static final Logger LOG = LoggerFactory.getLogger(ApiUtils.class);
	
	// Hide ctor
	private ApiUtils() {}
	
	public static JsonNode readJsonNode(Response res) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readTree(res.readEntity(String.class));
	}
	
	public static Status getStatus(Response res) {
		return Status.fromStatusCode(res.getStatus());
	}

	public static boolean hasStatus(Status exp, Response res) {
		if (exp == getStatus(res)) return true; 
		LOG.error("{} {}", res.getStatus(), res.getStatusInfo().getReasonPhrase());
		return false;
	}

	public static String portalURL(String path) {
		String url = TestPortProvider.generateURL("/portal" + path);
		return url;
	}

	public static String keycloakRoot() {
		String url = "http://yourhost:8080/auth";
		return url;
	}
	
	public static String keycloakURL(String path) {
		String url = keycloakRoot() + path;
		return url;
	}
	
	public static String keycloakRealmURL(String realm, String path) {
		String url = keycloakRoot() + "/realms/" + realm + path;
		return url;
	}
}
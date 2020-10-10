package io.nessus.actions.jaxrs;

import java.util.Arrays;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.nessus.common.AssertArg;
import io.nessus.common.AssertState;
import io.nessus.common.CheckedExceptionWrapper;

public final class ApiUtils {
	
	static final Logger LOG = LoggerFactory.getLogger(ApiUtils.class);
	
	// Hide ctor
	private ApiUtils() {}
	
	public static JsonNode readJsonNode(Response res) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String content = res.readEntity(String.class);
			return mapper.readTree(content);
		} catch (JsonProcessingException ex) {
			throw CheckedExceptionWrapper.create(ex);
		}
	}
	
	public static String errorMessageJson(String errmsg) {
		return String.format("{\"errorMessage\": \"%s\"}", errmsg);
	}

	public static String safeJson(Map<String, ? extends Object> map) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(map);
		} catch (JsonProcessingException ex) {
			throw CheckedExceptionWrapper.create(ex);
		}
	}

	public static Status getStatus(Response res) {
		return Status.fromStatusCode(res.getStatus());
	}

	public static boolean hasStatus(Response res, Status... exp) {
		AssertArg.notNull(exp, "Null expected");
		
		res.bufferEntity();
		
		if (Arrays.asList(exp).contains(getStatus(res))) { 
			return true;
		}
		
		JsonNode node = readJsonNode(res);
		JsonNode errNode = node.findValue("error_description");
		if (errNode == null) errNode = node.findValue("errorMessage");
		String errorText = errNode != null ? errNode.asText() : null;

		int status = res.getStatus();
		String reason = res.getStatusInfo().getReasonPhrase();
		if (errorText != null) {
			LOG.error("{} {} - {}", status, reason, errorText);
		} else {
			LOG.error("{} {}", status, reason);
		}
		return false;
	}

	public static String portalUrl() {
		ApiApplication api = ApiApplication.getInstance();
		String portalUrl = api.getConfig().getJaxrsUrl();
		AssertState.notNull(portalUrl, "Null portalUrl");
		return portalUrl;
	}

	public static String portalUrl(String path) {
		String url = portalUrl() + path;
		return url;
	}

	public static String keycloakUrl() {
		ApiApplication api = ApiApplication.getInstance();
		String keycloakUrl = api.getConfig().getKeycloakUrl();
		AssertState.notNull(keycloakUrl, "Null keycloakUrl");
		return keycloakUrl;
	}
	
	public static String keycloakUrl(String path) {
		String url = keycloakUrl() + path;
		return url;
	}
	
	public static String keycloakRealmUrl(String realm, String path) {
		String url = keycloakUrl() + "/realms/" + realm + path;
		return url;
	}
	
	public static String keycloakRealmTokenUrl(String realm) {
		return keycloakRealmTokenUrl(realm, "");
	}
	
	public static String keycloakRealmTokenUrl(String realm, String path) {
		String url = keycloakUrl() + "/realms/" + realm + "/protocol/openid-connect/token" + path;
		return url;
	}
}
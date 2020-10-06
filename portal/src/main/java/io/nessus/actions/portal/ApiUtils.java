package io.nessus.actions.portal;

import java.util.Arrays;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.nessus.actions.portal.resources.PortalApi;
import io.nessus.common.AssertArg;
import io.nessus.common.AssertState;

public final class ApiUtils {
	
	static final Logger LOG = LoggerFactory.getLogger(ApiUtils.class);
	
	// Hide ctor
	private ApiUtils() {}
	
	public static JsonNode readJsonNode(Response res) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String content = res.readEntity(String.class);
		return mapper.readTree(content);
	}
	
	public static Status getStatus(Response res) {
		return Status.fromStatusCode(res.getStatus());
	}

	public static boolean hasStatus(Response res, Status... exp) {
		AssertArg.notNull(exp, "Null expected");
		
		if (Arrays.asList(exp).contains(getStatus(res))) 
			return true;
		
		String errorText;
		try {
			JsonNode node = readJsonNode(res);
			JsonNode errNode = node.findValue("error_description");
			if (errNode == null) errNode = node.findValue("errorMessage");
			errorText = errNode != null ? errNode.asText() : null;
		} catch (JsonProcessingException ex) {
			errorText = null;
		}
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
		PortalApi api = PortalApi.getInstance();
		String portalUrl = api.getConfig().getPortalUrl();
		AssertState.notNull(portalUrl, "Null portalUrl");
		return portalUrl;
	}

	public static String portalUrl(String path) {
		String url = portalUrl() + path;
		return url;
	}

	public static String keycloakUrl() {
		PortalApi api = PortalApi.getInstance();
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
}
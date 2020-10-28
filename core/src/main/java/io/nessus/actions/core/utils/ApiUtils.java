package io.nessus.actions.core.utils;

import java.util.Arrays;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.nessus.actions.core.NessusConfig;
import io.nessus.common.AssertArg;
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
		} catch (Exception ex) {
			return null;
		}
	}
	
	public static String safeJson(Map<String, ? extends Object> map) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(map);
		} catch (JsonProcessingException ex) {
			throw CheckedExceptionWrapper.create(ex);
		}
	}

	public static boolean hasStatus(Response res, Status... exp) {
		AssertArg.notNull(exp, "Null expected");
		
		Status status = res.getStatusInfo().toEnum();
		if (Arrays.asList(exp).contains(status)) { 
			return true;
		}
		
		int code = status.getStatusCode();
		String reason = status.getReasonPhrase();
		ErrorMessage errmsg = getErrorMessage(res);
		
		if (errmsg != null) {
			LOG.error("{} {} - {}", code, reason, errmsg);
		} else {
			LOG.error("{} {}", code, reason);
		}
		return false;
	}

	public static ErrorMessage getErrorMessage(Response res) {
		ErrorMessage errmsg = null;
		JsonNode node = readJsonNode(res);
		if (node != null) {
			JsonNode errNode = node.findValue("error_description");
			if (errNode == null) 
				errNode = node.findValue("errorMessage");
			if (errNode != null) {
				String errtxt = errNode.asText();
				errmsg = new ErrorMessage(errtxt);
			}
		}
		return errmsg;
	}

	public static String keycloakUrl(NessusConfig config, String path) {
		String keycloakUrl = config.isUseTLS() ? config.getKeycloakTLSUrl() : config.getKeycloakUrl();
		return keycloakUrl + path;
	}
	
	public static String mavenUrl(NessusConfig config, String path) {
		String jaxrsUrl = config.isUseTLS() ? config.getMavenTLSUrl() : config.getMavenUrl();
		return jaxrsUrl + path;
	}
	
	public static String jaxrsUrl(NessusConfig config, String path) {
		String jaxrsUrl = config.isUseTLS() ? config.getJaxrsTLSUrl() : config.getJaxrsUrl();
		return jaxrsUrl + path;
	}
	
	public static String portalUrl(NessusConfig config, String path) {
		String jaxrsUrl = config.isUseTLS() ? config.getPortalTLSUrl() : config.getPortalUrl();
		return jaxrsUrl + path;
	}
	
	public static class ErrorMessage {
		
		public final String errorMessage;

		@JsonCreator
		public ErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
		}
		
		public String toString() {
			return errorMessage;
		}
	}
}
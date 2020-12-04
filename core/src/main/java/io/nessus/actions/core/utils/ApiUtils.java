package io.nessus.actions.core.utils;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.PrimitiveIterator.OfInt;
import java.util.Random;

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

	public static void assertStatus(Response res, Status... exp) {
		AssertState.isTrue(hasStatus(res, exp));
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

	public static String createIdentifier(String userId) {
		String prefix = userId.substring(0, userId.indexOf('-'));
		String result = String.format("%s-%s", prefix, createIdentifier(3));
		return result;
	}
	
	public static String createIdentifier(int blocks) {
		int digits = 6;
		String chars = "0123456789abcdef";
		OfInt rndint = new Random().ints(0, chars.length()).iterator();
		String result = "";
		for (int i = 0; i < blocks; i++) {
			result += "-";
			for (int j = 0; j < digits; j++) {
				result += chars.charAt(rndint.next());
			}
		}
		return result.substring(1);
	}
	
	public static URI keycloakUri(NessusConfig config, String path) {
		String keycloakUrl = config.isUseTLS() ? config.getKeycloakTLSUrl() : config.getKeycloakUrl();
		return URI.create(keycloakUrl + path);
	}
	
	public static URI mavenUri(NessusConfig config, String path) {
		String jaxrsUrl = config.isUseTLS() ? config.getMavenTLSUrl() : config.getMavenUrl();
		return URI.create(jaxrsUrl + path);
	}
	
	public static URI jaxrsUri(NessusConfig config, String path) {
		String jaxrsUrl = config.isUseTLS() ? config.getJaxrsTLSUrl() : config.getJaxrsUrl();
		return URI.create(jaxrsUrl + path);
	}
	
	public static URI portalUri(NessusConfig config, String path) {
		String jaxrsUrl = config.isUseTLS() ? config.getPortalTLSUrl() : config.getPortalUrl();
		return URI.create(jaxrsUrl + path);
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
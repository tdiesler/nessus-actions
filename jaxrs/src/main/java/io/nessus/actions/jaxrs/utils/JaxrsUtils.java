package io.nessus.actions.jaxrs.utils;

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

import io.nessus.actions.jaxrs.ApiApplication;
import io.nessus.actions.jaxrs.JaxrsConfig;
import io.nessus.common.AssertArg;
import io.nessus.common.CheckedExceptionWrapper;

public final class JaxrsUtils {
	
	static final Logger LOG = LoggerFactory.getLogger(JaxrsUtils.class);
	
	// Hide ctor
	private JaxrsUtils() {}
	
	public static JsonNode readJsonNode(Response res) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String content = res.readEntity(String.class);
			return mapper.readTree(content);
		} catch (JsonProcessingException ex) {
			throw CheckedExceptionWrapper.create(ex);
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
		
		res.bufferEntity();
		
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
		JsonNode node = readJsonNode(res);
		JsonNode errNode = node.findValue("error_description");
		if (errNode == null) errNode = node.findValue("errorMessage");
		String errmsg = errNode != null ? errNode.asText() : null;
		return errmsg != null ? new ErrorMessage(errmsg) : null;
	}

	public static String jaxrsUrl(String path, boolean tls) {
		JaxrsConfig config = ApiApplication.getInstance().getConfig();
		String jaxrsUrl = tls ? config.getJaxrsTLSUrl() : config.getJaxrsUrl();
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
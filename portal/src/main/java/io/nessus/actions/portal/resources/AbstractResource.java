package io.nessus.actions.portal.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.nessus.actions.model.utils.CheckedExceptionWrapper;

public class AbstractResource {
	
	protected final Logger LOG = LoggerFactory.getLogger(getClass());
	
	public static String asText(String json, String path) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(json);
			JsonNode value = root.findValue(path);
			return value != null ? value.asText() : null;
		} catch (JsonProcessingException ex) {
			throw CheckedExceptionWrapper.create(ex);
		}
	}
	
	public static String toJson(Object value) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(value);
		} catch (JsonProcessingException ex) {
			throw CheckedExceptionWrapper.create(ex);
		}
	}

	static class MessageResponse {

		final String msg;

		MessageResponse(String msg) {
			this.msg = msg;
		}

		@JsonGetter("msg")
		String getMessage() {
			return msg;
		}
	}
}
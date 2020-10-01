package io.nessus.actions.portal.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonGetter;

abstract class AbstractResource {
	
	protected final Logger LOG = LoggerFactory.getLogger(getClass());
	
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
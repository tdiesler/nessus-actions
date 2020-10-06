package io.nessus.actions.portal.resources;

import com.fasterxml.jackson.annotation.JsonGetter;

import io.nessus.actions.portal.PortalConfig;
import io.nessus.common.ConfigSupport;

abstract class AbstractResource extends ConfigSupport<PortalConfig> {
	
	protected PortalApi api;
	
	AbstractResource(PortalApi api) {
		super(api.getConfig());
		this.api = api;
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
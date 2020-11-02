package io.nessus.actions.jaxrs.type;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.nessus.actions.core.model.RouteModel;
import io.nessus.common.AssertArg;

public class UserModelBase {
	
	public final String userId;
	public final String content;
	public RouteModel routeModel;
	
	public UserModelBase(String userId, String content) {
		AssertArg.notNull(userId, "Null userId");
		this.userId = userId;
		this.content = content;
	}

	public String getUserId() {
		return userId;
	}

	@JsonIgnore
	public String getTitle() {
		return getRouteModel().getTitle();
	}

	public String getContent() {
		return content;
	}
	
	@JsonIgnore
	public RouteModel getRouteModel() {
		if (routeModel == null) {
			routeModel = RouteModel.read(content);
		}
		return routeModel;
	}
}
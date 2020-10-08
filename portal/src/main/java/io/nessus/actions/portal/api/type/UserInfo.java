package io.nessus.actions.portal.api.type;

public class UserInfo {
	
	private String status;
	private String name;
	private String username;
	private String email;
	private boolean emailVerified;

	public UserInfo() {
	}
	
	public UserInfo(KeycloakUserInfo kcinfo) {
		this.status = "Active";
		this.name = kcinfo.name;
		this.username = kcinfo.username;
		this.email = kcinfo.email;
		this.emailVerified = kcinfo.emailVerified;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isEmailVerified() {
		return emailVerified;
	}

	public void setEmailVerified(boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

}
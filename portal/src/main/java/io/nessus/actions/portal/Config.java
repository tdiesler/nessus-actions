package io.nessus.actions.portal;

public class Config {

	private static Config INSTANCE = new Config();
	
	private final String realmId = "myrealm";
	private final String clientId = "myclient";
	private final String username = "myuser";
	private final String password = "mypass";
	
	public static Config getInstance() {
		return INSTANCE;
	}

	public String getRealmId() {
		return realmId;
	}

	public String getClientId() {
		return clientId;
	}

	public String getClientSecret() {
		return "ebe8fa51-53d4-4836-8692-01b2ebc05006";
	}
	
	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}

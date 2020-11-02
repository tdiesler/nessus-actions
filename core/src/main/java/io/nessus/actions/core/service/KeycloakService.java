package io.nessus.actions.core.service;

import static io.nessus.actions.core.utils.KeycloakUtils.keycloakRealmPath;
import static io.nessus.actions.core.utils.KeycloakUtils.keycloakRealmTokenPath;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.nessus.actions.core.NessusConfig;
import io.nessus.actions.core.types.KeycloakTokenInfo;
import io.nessus.actions.core.types.KeycloakUserInfo;
import io.nessus.actions.core.utils.ApiUtils;
import io.nessus.actions.core.utils.ApiUtils.ErrorMessage;
import io.nessus.common.AssertArg;
import io.nessus.common.AssertState;

public class KeycloakService extends AbstractService<NessusConfig> {

	public KeycloakService(NessusConfig config) {
		super(config);
	}

	public String getMasterAccessToken() {
		
		String refreshToken = getMasterRefreshToken();
		String accessToken = refreshMasterAccessToken(refreshToken);
		
		return accessToken;
	}

	public String refreshMasterAccessToken(String refreshToken) {
		
		String accessToken = refreshMasterAccessTokenInternal(refreshToken);
		
		if (accessToken == null) {
			refreshToken = createMasterRefreshToken();
			accessToken = refreshMasterAccessTokenInternal(refreshToken);
		}
		
		return accessToken;
	}

	private String refreshMasterAccessTokenInternal(String refreshToken) {
		AssertArg.notNull(refreshToken, "Null refreshToken");
		
		logInfo("Refresh master access token ...");
		
		MultivaluedHashMap<String, String> data = new MultivaluedHashMap<>();
		data.add("client_id", "admin-cli");
		data.add("refresh_token", refreshToken);
		data.add("grant_type", "refresh_token");
		
		URI uri = ApiUtils.keycloakUri(config, keycloakRealmTokenPath("master"));
		Response res = withClient(uri, target -> target.request().post(Entity.form(data)));
		
		if (!ApiUtils.hasStatus(res, Status.OK)) {
			return null;
		}
		
		@SuppressWarnings("unchecked")
		Map<String, String> resmap = res.readEntity(LinkedHashMap.class);
		String accessToken = resmap.get("access_token");
		
		return accessToken;
	}

	public String getMasterRefreshToken() {
		
		String masterRefreshToken = config.getKeycloakRefreshToken();
		if (masterRefreshToken == null) {
			masterRefreshToken = createMasterRefreshToken();
		}
		
		return masterRefreshToken;
	}

	private String createMasterRefreshToken() {
		
		logInfo("Create master refresh token ...");
		
		String masterUsername = config.getMasterUser();
		String masterPassword = config.getMasterPassword();
		
		boolean valid = (masterUsername != null && masterPassword != null);
		AssertState.isTrue(valid, "Master username/massword required");
		
		MultivaluedHashMap<String, String> data = new MultivaluedHashMap<>();
		data.add("client_id", "admin-cli");
		data.add("username", masterUsername);
		data.add("password", masterPassword);
		data.add("grant_type", "password");
		
		URI uri = ApiUtils.keycloakUri(config, keycloakRealmTokenPath("master"));
		Response res = withClient(uri, target -> target.request()
				.post(Entity.form(data)));
		
		if (!ApiUtils.hasStatus(res, Status.OK))
			return null;
		
		@SuppressWarnings("unchecked")
		Map<String, String> resmap = ((Map<String, String>) res.readEntity(LinkedHashMap.class));
		String masterRefreshToken = resmap.get("refresh_token");
		
		config.putParameter("masterRefreshToken", masterRefreshToken);
		return masterRefreshToken;
	}

	public String refreshAccessToken(String refreshToken) {
		
		String realmId = config.getKeycloakRealmId();
		String clientId = config.getKeycloakClientId();
		return refreshAccessToken(realmId, clientId, refreshToken);
	}
	
	public String refreshAccessToken(String realmId, String clientId, String refreshToken) {
		AssertArg.notNull(realmId, "Null realm");
		AssertArg.notNull(clientId, "Null clientId");
		AssertArg.notNull(refreshToken, "Null refreshToken");
		
		logInfo("Refresh access token [realm={}, client={}] ...", realmId, clientId);
		
		String clientSecret = getClientSecret(realmId, clientId);
		
		MultivaluedHashMap<String, String> data = new MultivaluedHashMap<>();
		data.add("client_id", clientId);
		data.add("client_secret", clientSecret);
		data.add("refresh_token", refreshToken);
		data.add("grant_type", "refresh_token");
		
		URI uri = ApiUtils.keycloakUri(config, keycloakRealmTokenPath(realmId));
		Response res = withClient(uri, target -> target.request()
				.post(Entity.form(data)));
		
		if (!ApiUtils.hasStatus(res, Status.OK))
			return null;
		
		@SuppressWarnings("unchecked")
		Map<String, String> resmap = res.readEntity(LinkedHashMap.class);
		String accessToken = resmap.get("access_token");
		
		return accessToken;
	}

	@SuppressWarnings("unchecked")
	public String getClientSecret(String realmId, String clientId) {

		String clientSecret = config.getParameter("clientSecret", String.class);
		if (clientSecret == null) {
			
			logInfo("Get client secret [realm={}, client={}] ...", realmId, clientId);
			
			String accessToken = getMasterAccessToken();
			
			URI uri = ApiUtils.keycloakUri(config, String.format("/admin/realms/%s/clients?clientId=%s", realmId, clientId));
			Response res = withClient(uri, target -> target.request()
					.header("Authorization", "Bearer " + accessToken).get());
			
			JsonNode node = ApiUtils.readJsonNode(res);
			AssertState.isTrue(node.isArray(), "Not an array node: " + node);
			String id = ((ArrayNode) node).get(0).findValue("id").asText();

			// GET the client secret
			
			uri = ApiUtils.keycloakUri(config, String.format("/admin/realms/%s/clients/%s/client-secret", realmId, id));
			res = withClient(uri, target -> target.request()
					.header("Authorization", "Bearer " + accessToken).get());
			
			if (!ApiUtils.hasStatus(res, Status.OK))
				return null;
			
			Map<String, String> resmap = res.readEntity(LinkedHashMap.class);
			clientSecret = resmap.get("value");
			
			// This would be the default import secret
			
			if (clientSecret.equals("**********")) {
				
				// POST to update the client secret
				
				res = withClient(uri, target -> target.request()
							.header("Authorization", "Bearer " + accessToken)
							.post(null));
				
				if (!ApiUtils.hasStatus(res, Status.OK))
					return null;
				
				resmap = res.readEntity(LinkedHashMap.class);
				clientSecret = resmap.get("value");
			}
			
			config.putParameter("clientSecret", clientSecret);
		}
		
		return clientSecret;
	}

	public Response getUserTokens(String username, String password) {
		
		logInfo("Get user tokens [user={}] ...", username);
		
		String realmId = config.getKeycloakRealmId();
		String clientId = config.getKeycloakClientId();
		String clientSecret = getClientSecret(realmId, clientId);
		
		MultivaluedHashMap<String, String> data = new MultivaluedHashMap<>();
		data.add("client_id", clientId);
		data.add("client_secret", clientSecret);
		data.add("username", username);
		data.add("password", password);
		data.add("grant_type", "password");
		
		URI uri = ApiUtils.keycloakUri(config, keycloakRealmTokenPath(realmId));
		Response res = withClient(uri, target -> target.request()
				.post(Entity.form(data)));
		
		ApiUtils.hasStatus(res, Status.OK);
		
		return res;
	}

	public Response getKeycloakUserInfo(String accessToken) {
		
		String realmId = config.getKeycloakRealmId();

		URI uri = ApiUtils.keycloakUri(config, keycloakRealmPath(realmId, "/protocol/openid-connect/userinfo"));
		Response res = withClient(uri, target -> target.request()
					.header("Authorization", "Bearer " + accessToken)
					.get());
		
		if (!ApiUtils.hasStatus(res, Status.OK)) {
			return res;
		}

		KeycloakUserInfo uinfo = res.readEntity(KeycloakUserInfo.class);
		logInfo("KeycloakUserInfo [user={}, email={}]", uinfo.username, uinfo.email);
		
		return res;
	}

	public Response introspectToken(String accessToken) {
		
		String realmId = config.getKeycloakRealmId();
		String clientId = config.getKeycloakClientId();
		String clientSecret = getClientSecret(realmId, clientId);

		MultivaluedMap<String, String> reqmap = new MultivaluedHashMap<>();
		reqmap.add("client_id", clientId);
		reqmap.add("client_secret", clientSecret);
		reqmap.add("token", accessToken);
		
		URI uri = ApiUtils.keycloakUri(config, keycloakRealmTokenPath(realmId, "/introspect"));
		Response res = withClient(uri, target -> target.request()
				.post(Entity.form(reqmap)));
		
		if (!ApiUtils.hasStatus(res, Status.OK)) {
			return res;
		}

		KeycloakTokenInfo kctoken = res.readEntity(KeycloakTokenInfo.class);
		logInfo("Introspect token [user={}, email={}] ...", kctoken.username, kctoken.email);
		
		if (!kctoken.active) {
			ErrorMessage errmsg = new ErrorMessage("Token is not active");
			res = Response.status(Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON).entity(errmsg).build();
		}
		
		return res;
	}
}
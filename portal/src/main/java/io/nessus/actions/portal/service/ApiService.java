package io.nessus.actions.portal.service;

import static io.nessus.actions.portal.api.ApiUtils.errorMessageJson;
import static io.nessus.actions.portal.api.ApiUtils.hasStatus;
import static io.nessus.actions.portal.api.ApiUtils.keycloakRealmTokenUrl;
import static io.nessus.actions.portal.api.ApiUtils.keycloakRealmUrl;
import static io.nessus.actions.portal.api.ApiUtils.keycloakUrl;
import static io.nessus.actions.portal.api.ApiUtils.readJsonNode;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.nessus.actions.portal.PortalConfig;
import io.nessus.actions.portal.api.type.KeycloakTokenInfo;
import io.nessus.actions.portal.utils.DateUtils;
import io.nessus.common.AssertArg;
import io.nessus.common.AssertState;

public class ApiService extends AbstractService {

	public ApiService(PortalConfig config) {
		super(config);
	}

	public Response withClient(String uri, Function<WebTarget, Response> function) {
		Client client = ClientBuilder.newClient();
		Date tsBefore = new Date();
		try {
			WebTarget target = client.target(uri);
			Response res = function.apply(target);
			int status = res.getStatus();
			String reason = res.getStatusInfo().getReasonPhrase();
			logInfo("{} => [{} {}] in {}ms", uri, status, reason, DateUtils.elapsedTime(tsBefore));
			return res;
		} finally {
			client.close();
		}
	}
	
	public String getMasterAccessToken() {
		
		String refreshToken = getMasterRefreshToken();
		String accessToken = refreshMasterAccessToken(refreshToken);
		
		return accessToken;
	}

	public String refreshMasterAccessToken(String refreshToken) {
		AssertArg.notNull(refreshToken, "Null refreshToken");
		
		MultivaluedHashMap<String, String> data = new MultivaluedHashMap<>();
		data.add("client_id", "admin-cli");
		data.add("refresh_token", refreshToken);
		data.add("grant_type", "refresh_token");
		
		Response res = withClient(keycloakRealmTokenUrl("master"),
				target -> target.request().post(Entity.form(data)));
		
		if (!hasStatus(res, Status.OK))
			return null;
		
		@SuppressWarnings("unchecked")
		Map<String, String> resmap = res.readEntity(LinkedHashMap.class);
		String accessToken = resmap.get("access_token");
		
		return accessToken;
	}

	public String getMasterRefreshToken() {
		
		String masterRefreshToken = config.getMasterRefreshToken();
		if (masterRefreshToken == null) {
			
			String masterUsername = config.getMasterUsername();
			String masterPassword = config.getMasterPassword();
			
			boolean valid = (masterUsername != null && masterPassword != null);
			AssertState.isTrue(valid, "Master username/massword required");
			
			MultivaluedHashMap<String, String> data = new MultivaluedHashMap<>();
			data.add("client_id", "admin-cli");
			data.add("username", masterUsername);
			data.add("password", masterPassword);
			data.add("grant_type", "password");
			
			Response res = withClient(keycloakRealmTokenUrl("master"),
					target -> target.request().post(Entity.form(data)));
			
			if (!hasStatus(res, Status.OK))
				return null;
			
			@SuppressWarnings("unchecked")
			Map<String, String> resmap = ((Map<String, String>) res.readEntity(LinkedHashMap.class));
			masterRefreshToken = resmap.get("refresh_token");
			
			config.putParameter("masterRefreshToken", masterRefreshToken);
		}
		
		return masterRefreshToken;
	}

	public String refreshAccessToken(String refreshToken) {
		
		String realmId = config.getRealmId();
		String clientId = config.getClientId();
		return refreshAccessToken(realmId, clientId, refreshToken);
	}
	
	public String refreshAccessToken(String realmId, String clientId, String refreshToken) {
		AssertArg.notNull(realmId, "Null realm");
		AssertArg.notNull(clientId, "Null clientId");
		AssertArg.notNull(refreshToken, "Null refreshToken");
		
		String clientSecret = getClientSecret(realmId, clientId);
		
		MultivaluedHashMap<String, String> data = new MultivaluedHashMap<>();
		data.add("client_id", clientId);
		data.add("client_secret", clientSecret);
		data.add("refresh_token", refreshToken);
		data.add("grant_type", "refresh_token");
		
		Response res = withClient(keycloakRealmTokenUrl(realmId),
				target -> target.request().post(Entity.form(data)));
		
		if (!hasStatus(res, Status.OK))
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
			
			String accessToken = getMasterAccessToken();
			
			Response res = withClient(keycloakUrl(String.format("/admin/realms/%s/clients?clientId=%s", realmId, clientId)), 
					target -> target.request().header("Authorization", "Bearer " + accessToken).get());
			
			JsonNode node = readJsonNode(res);
			AssertState.isTrue(node.isArray(), "Not an array node: " + node);
			String id = ((ArrayNode) node).get(0).findValue("id").asText();

			// GET the client secret
			
			res = withClient(keycloakUrl(String.format("/admin/realms/%s/clients/%s/client-secret", realmId, id)), 
					target -> target.request().header("Authorization", "Bearer " + accessToken).get());
			
			if (!hasStatus(res, Status.OK))
				return null;
			
			Map<String, String> resmap = res.readEntity(LinkedHashMap.class);
			clientSecret = resmap.get("value");
			
			// This would be the default import secret
			
			if (clientSecret.equals("**********")) {
				
				// POST to update the client secret
				
				res = withClient(keycloakUrl(String.format("/admin/realms/%s/clients/%s/client-secret", realmId, id)), 
						target -> target.request()
							.header("Authorization", "Bearer " + accessToken)
							.post(null));
				
				if (!hasStatus(res, Status.OK))
					return null;
				
				resmap = res.readEntity(LinkedHashMap.class);
				clientSecret = resmap.get("value");
			}
			
			config.putParameter("clientSecret", clientSecret);
		}
		
		return clientSecret;
	}

	public Response getUserTokens(String username, String password) {
		
		// Get the user's access/refresh token
		
		String realmId = config.getRealmId();
		String clientId = config.getClientId();
		String clientSecret = getClientSecret(realmId, clientId);
		
		MultivaluedHashMap<String, String> data = new MultivaluedHashMap<>();
		data.add("client_id", clientId);
		data.add("client_secret", clientSecret);
		data.add("username", username);
		data.add("password", password);
		data.add("grant_type", "password");
		
		Response res = withClient(keycloakRealmTokenUrl(realmId),
				target -> target.request().post(Entity.form(data)));
		
		if (!hasStatus(res, Status.OK)) {
			int status = res.getStatus();
			String reason = res.getStatusInfo().getReasonPhrase();
			return Response.status(status, reason).build();
		}
		
		return res;
	}

	public Response getUserInfo(String accessToken) {
		
		String realmId = config.getRealmId();

		// Get the user info
		
		Response res = withClient(keycloakRealmUrl(realmId, "/protocol/openid-connect/userinfo"), 
				target -> target.request()
					.header("Authorization", "Bearer " + accessToken)
					.get());
		
		if (!hasStatus(res, Status.OK)) {
			return res;
		}

		// You can now read ...
		// res.readEntity(KeycloakUserInfo.class);
		
		return res;
	}

	public Response introspectToken(String accessToken) {
		
		String realmId = config.getRealmId();
		String clientId = config.getClientId();
		String clientSecret = getClientSecret(realmId, clientId);

		// Introspect the given token
		
		MultivaluedMap<String, String> reqmap = new MultivaluedHashMap<>();
		reqmap.add("client_id", clientId);
		reqmap.add("client_secret", clientSecret);
		reqmap.add("token", accessToken);
		
		Response res = withClient(keycloakRealmTokenUrl(realmId, "/introspect"), 
				target -> target.request().post(Entity.form(reqmap)));
		
		if (!hasStatus(res, Status.OK)) {
			return res;
		}

		KeycloakTokenInfo kctoken = res.readEntity(KeycloakTokenInfo.class);
		if (!kctoken.active) {
			String errmsg = errorMessageJson("non active token");
			res = Response.status(Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON).entity(errmsg).build();
		}
		
		return res;
	}
}
package io.nessus.actions.portal.api;

import static io.nessus.actions.portal.api.ApiUtils.hasStatus;
import static io.nessus.actions.portal.api.ApiUtils.keycloakRealmUrl;
import static io.nessus.actions.portal.api.ApiUtils.keycloakUrl;
import static io.nessus.actions.portal.api.ApiUtils.withClient;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.nessus.actions.portal.PortalConfig;
import io.nessus.common.AssertArg;
import io.nessus.common.AssertState;
import io.nessus.common.CheckedExceptionWrapper;

public class ApiService extends AbstractPortalService {

	public ApiService(PortalConfig config) {
		super(config);
	}

	public String getMasterAccessToken() {
		
		String refreshToken = getMasterRefreshToken();
		String accessToken = refreshAccessToken("master", "admin-cli", refreshToken);
		
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
			
			Response res = withClient(client -> client
					.target(keycloakUrl() + "/realms/master/protocol/openid-connect/token")
					.request().post(Entity.form(data)));
			
			if (!hasStatus(res, Status.OK))
				return null;
			
			@SuppressWarnings("unchecked")
			Map<String, String> resmap = ((Map<String, String>) res.readEntity(LinkedHashMap.class));
			masterRefreshToken = resmap.get("refresh_token");
			
			config.putParameter("masterRefreshToken", masterRefreshToken);
		}
		
		return masterRefreshToken;
	}

	public String refreshAccessToken(String realmId, String clientId, String refreshToken) {
		AssertArg.notNull(realmId, "Null realm");
		AssertArg.notNull(clientId, "Null clientId");
		AssertArg.notNull(refreshToken, "Null refreshToken");
		
		MultivaluedHashMap<String, String> data = new MultivaluedHashMap<>();
		data.add("client_id", clientId);
		data.add("refresh_token", refreshToken);
		data.add("grant_type", "refresh_token");
		
		Response res = withClient(client -> client
				.target(keycloakUrl("/realms/" + realmId + "/protocol/openid-connect/token"))
				.request().post(Entity.form(data)));
		
		if (!hasStatus(res, Status.OK))
			return null;
		
		@SuppressWarnings("unchecked")
		Map<String, String> resmap = res.readEntity(LinkedHashMap.class);
		String accessToken = resmap.get("access_token");
		
		return accessToken;
	}

	@SuppressWarnings("unchecked")
	public String getClientSecret(String realmId, String clientId) {

		String accessToken = getMasterAccessToken();
		
		Response res = ApiUtils.withClient(client -> client
				.target(keycloakUrl(String.format("/admin/realms/%s/clients?clientId=%s", realmId, clientId)))
				.request().header("Authorization", "Bearer " + accessToken).get());
		
		JsonNode node = readJsonNode(res);
		AssertState.isTrue(node.isArray(), "Not an array node: " + node);
		String id = ((ArrayNode) node).get(0).findValue("id").asText();

		// GET the client secret
		
		res = withClient(client -> client
				.target(keycloakUrl(String.format("/admin/realms/%s/clients/%s/client-secret", realmId, id)))
				.request().header("Authorization", "Bearer " + accessToken).get());
		
		if (!hasStatus(res, Status.OK))
			return null;
		
		Map<String, String> resmap = res.readEntity(LinkedHashMap.class);
		String clientSecret = resmap.get("value");
		
		// This would be the default import secret
		
		if (clientSecret.equals("**********")) {
			
			// POST to update the client secret
			
			res = withClient(client -> client
					.target(keycloakUrl(String.format("/admin/realms/%s/clients/%s/client-secret", realmId, id)))
					.request().header("Authorization", "Bearer " + accessToken).post(null));
			
			if (!hasStatus(res, Status.OK))
				return null;
			
			resmap = res.readEntity(LinkedHashMap.class);
			clientSecret = resmap.get("value");
		}
		
		return clientSecret;
	}

	public Response userLogin(String username, String password) {
		
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
		
		Response res = withClient(client -> client
				.target(keycloakRealmUrl(realmId, "/protocol/openid-connect/token"))
				.request().post(Entity.form(data)));
		
		if (!hasStatus(res, Status.OK)) {
			int status = res.getStatus();
			String reason = res.getStatusInfo().getReasonPhrase();
			return Response.status(status, reason).build();
		}
		
		@SuppressWarnings("unchecked")
		Map<String, String> resmap = ((Map<String, String>) res.readEntity(LinkedHashMap.class)).entrySet().stream()
			.filter(en -> en.getKey().equals("refresh_token"))
			.collect(Collectors.toMap(en -> en.getKey(), en -> en.getValue()));
		
		resmap.put("username", username);

		return Response.ok(resmap, MediaType.APPLICATION_JSON).build();
	}
	
	private JsonNode readJsonNode(Response res) {
		try {
			return ApiUtils.readJsonNode(res);
		} catch (JsonProcessingException ex) {
			throw CheckedExceptionWrapper.create(ex);
		}
	}
}
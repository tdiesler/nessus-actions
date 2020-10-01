package io.nessus.actions.portal.resources;

import static io.nessus.actions.portal.ApiUtils.hasStatus;
import static io.nessus.actions.portal.ApiUtils.keycloakRealmURL;
import static io.nessus.actions.portal.ApiUtils.keycloakURL;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.nessus.actions.model.utils.AssertState;
import io.nessus.actions.portal.ApiUtils;

@ApplicationPath("/portal")
public class PortalApi extends Application {
	
	static final Logger LOG = LoggerFactory.getLogger(PortalApi.class);
	
	private static PortalApi INSTANCE;
	
	public PortalApi() {
		AssertState.isNull(INSTANCE);
		INSTANCE = this;
	}
	
	public static PortalApi getInstance() {
		return INSTANCE;
	}
	
	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<>();
		classes.add(PortalStatus.class);
		classes.add(UserRegister.class);
		return Collections.unmodifiableSet(classes);
	}

	public String getMasterAccessToken() {
		return getAccessToken("master", "admin-cli", null, "admin", "admin");
	}

	public String getAccessToken(String realm, String clientId, String clientSecret, String username, String password) {
		
		// [TODO] This should only be done once
		// After that, we could only need to refresh the token
		
		MultivaluedHashMap<String, String> data = new MultivaluedHashMap<>();
		data.add("client_id", clientId);
		data.add("client_secret", clientSecret);
		data.add("username", username);
		data.add("password", password);
		data.add("grant_type", "password");
		
		Response res = ClientBuilder.newClient()
				.target(ApiUtils.keycloakURL("/realms/" + realm + "/protocol/openid-connect/token"))
				.request(MediaType.APPLICATION_FORM_URLENCODED)
				.post(Entity.form(data));
		
		if (!hasStatus(Status.OK, res))
			return null;
		
		@SuppressWarnings("unchecked")
		Map<String, String> resmap = res.readEntity(LinkedHashMap.class);
		String accessToken = resmap.get("access_token");
		
		return accessToken;
	}

	public String getClientSecret(String realmId, String clientId) throws JsonProcessingException {
		
		// Get master access token
		PortalApi portal = PortalApi.getInstance();
		String accessToken = portal.getMasterAccessToken();

		// Get client id

		Response res = ClientBuilder.newClient()
				.target(keycloakURL("/admin/realms/myrealm/clients?clientId=" + clientId))
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)
				.get();
		
		if (!hasStatus(Status.OK, res))
			return null;
		
		TreeNode treeNode = ApiUtils.readJsonNode(res);
		AssertState.isTrue(treeNode.isArray(), "Not an array node: " + treeNode);
		
		JsonNode jsonNode = ((ArrayNode) treeNode).get(0);
		String id = jsonNode.findPath("id").asText();
		LOG.info("{}: {}", clientId, id);
		
		// Get client secret
		
		res = ClientBuilder.newClient()
				.target(keycloakRealmURL(realmId, "/clients/" + id + "/client-secret"))
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)
				.get();
		
		if (!hasStatus(Status.OK, res))
			return null;

		jsonNode = ApiUtils.readJsonNode(res);
		String secret = jsonNode.findPath("value").asText();
		return secret;
	}
}
package io.nessus.actions.portal.api;

import static io.nessus.actions.portal.api.ApiUtils.hasStatus;
import static io.nessus.actions.portal.api.ApiUtils.keycloakRealmUrl;
import static io.nessus.actions.portal.api.ApiUtils.keycloakUrl;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.nessus.actions.portal.PortalApi;
import io.nessus.actions.portal.PortalConfig;

/**
 * User self registration requires the a master access token.
 * 
 * Here is the (unfortunate) reason why 
 * https://issues.redhat.com/browse/KEYCLOAK-15820
 * 
 * 1) Request a master access token
 * 2) Post the user create request
 * 3) Get the refresh token for the user
 * 3) Create the user session (cookie)
 *  
 */
@Path("/users")
public class ApiUserRegister extends AbstractApiResource {
	
	@Context
	private HttpServletRequest servletRequest;
	
	public ApiUserRegister() {
		super(PortalApi.getInstance());
	}

	@POST
	public Response register(User user) {
		
		LOG.info("Register: {}", user.getEmail());
		
		ApiService apisrv = api.getApiService();
		PortalConfig config = api.getConfig();
		
		String realmId = config.getRealmId();
		String accessToken = apisrv.getMasterAccessToken();
		
		// Create the user record
		
		Response regres = ClientBuilder.newClient()
				.target(keycloakUrl("/admin/realms/" + realmId + "/users"))
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)
				.post(Entity.json(new KeycloakUser(user)));
		
		if (!hasStatus(regres, Status.CREATED)) {
			int status = regres.getStatus();
			String reason = regres.getStatusInfo().getReasonPhrase();
			return Response.status(status, reason).build();
		}
		
		// Get the user's access/refresh token
		
		String clientId = config.getClientId();
		String clientSecret = apisrv.getClientSecret(realmId, clientId);
		
		MultivaluedHashMap<String, String> data = new MultivaluedHashMap<>();
		data.add("client_id", clientId);
		data.add("client_secret", clientSecret);
		data.add("username", user.getUsername());
		data.add("password", user.getPassword());
		data.add("grant_type", "password");
		
		Response tokres = ClientBuilder.newClient()
				.target(keycloakRealmUrl(realmId, "/protocol/openid-connect/token"))
				.request().post(Entity.form(data));
		
		if (!hasStatus(tokres, Status.OK)) {
			int status = regres.getStatus();
			String reason = regres.getStatusInfo().getReasonPhrase();
			return Response.status(status, reason).build();
		}
		
		@SuppressWarnings("unchecked")
		Map<String, String> resmap = ((Map<String, String>) tokres.readEntity(LinkedHashMap.class));
		String refreshToken = resmap.get("refresh_token");
		
		// Create the session and attach it to the response
		
		HttpSession session = servletRequest.getSession(true);
		session.setAttribute("refresh_token", refreshToken);
		
		return Response.status(Status.CREATED).build();
	}

	public static class User {
		
		final String username;
		final String firstName;
		final String lastName;
		final String email;
		final String password;
		
		@JsonCreator
		public User(
			@JsonProperty("firstName") String firstName, 
			@JsonProperty("lastName") String lastName, 
			@JsonProperty("email") String email, 
			@JsonProperty("username") String username, 
			@JsonProperty("password") String password) {
			this.username = username;
			this.firstName = firstName;
			this.lastName = lastName;
			this.email = email;
			this.password = password;
		}

		public String getUsername() {
			return username;
		}

		public String getFirstName() {
			return firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public String getEmail() {
			return email;
		}

		public String getPassword() {
			return password;
		}
	}

	public static class KeycloakUser {
		
		final String username;
		final String firstName;
		final String lastName;
		final String email;
		final List<KeycloakCredentials> credentials;
		final Boolean enabled;
		
		@JsonCreator
		KeycloakUser(@JsonProperty("username") String username, 
			@JsonProperty("firstName") String firstName, 
			@JsonProperty("lastName") String lastName, 
			@JsonProperty("email") String email, 
			@JsonProperty("credentials") List<KeycloakCredentials> credentials,
			@JsonProperty("enabled") Boolean enabled) {
			this.username = username;
			this.firstName = firstName;
			this.lastName = lastName;
			this.email = email;
			this.credentials = credentials;
			this.enabled = enabled;
		}

		public KeycloakUser(User user) {
			this.username = user.username;
			this.firstName = user.firstName;
			this.lastName = user.lastName;
			this.email = user.email;
			this.credentials = Arrays.asList(new KeycloakCredentials("password", user.password, false));
			this.enabled = true;
		}

		public String getUsername() {
			return username;
		}

		public String getFirstName() {
			return firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public String getEmail() {
			return email;
		}

		public List<KeycloakCredentials> getCredentials() {
			return credentials;
		}

		public Boolean getEnabled() {
			return enabled;
		}
	}

	public static class KeycloakCredentials {
		
		final String type;
		final String value;
		final boolean temporary;
		
		@JsonCreator
		KeycloakCredentials(
			@JsonProperty("type") String type, 
			@JsonProperty("value") String value, 
			@JsonProperty("temporary") boolean temporary) {
			this.type = type;
			this.value = value;
			this.temporary = temporary;
		}

		public String getType() {
			return type;
		}

		public String getValue() {
			return value;
		}

		public boolean isTemporary() {
			return temporary;
		}
	}
}
package io.nessus.actions.portal.resources;

import static io.nessus.actions.portal.ApiUtils.keycloakURL;
import static io.nessus.actions.portal.ApiUtils.hasStatus;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Path("/api/users")
public class UserRegister extends AbstractResource {
	
	@POST
	public Response register(User user) {
		
		LOG.info("Register: {}", user.getEmail());
		
		KeycloakUser kcuser = new KeycloakUser(user);
		
		PortalApi api = PortalApi.getInstance();
		String accessToken = api.getMasterAccessToken();
		
		Response res = ClientBuilder.newClient()
				.target(keycloakURL("/admin/realms/myrealm/users"))
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)
				.post(Entity.json(kcuser));
		
		hasStatus(Status.CREATED, res);
		
		return res;
	}

	public static class User {
		
		final String username;
		final String firstName;
		final String lastName;
		final String email;
		final String password;
		
		@JsonCreator
		User(@JsonProperty("username") String username, 
			@JsonProperty("firstName") String firstName, 
			@JsonProperty("lastName") String lastName, 
			@JsonProperty("email") String email, 
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
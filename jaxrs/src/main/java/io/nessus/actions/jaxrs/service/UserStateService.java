package io.nessus.actions.jaxrs.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import io.nessus.actions.core.NessusConfig;
import io.nessus.actions.core.types.KeycloakUserInfo;
import io.nessus.actions.jaxrs.type.UserState;

public class UserStateService extends AbstractJaxrsService {

	public UserStateService(NessusConfig config) {
		super(config);
	}

	public UserState getOrCreateUserState(KeycloakUserInfo kcinfo) {
		
		return withConnection(con -> {
			
			UserState userState = getUserState(con, kcinfo);
			if (userState == null) {
				userState = createUserState(con, kcinfo);
			}
			
			return userState;
		});
	}

	public UserState userLogin(KeycloakUserInfo kcinfo) {
		
		return withConnection(con -> {

			UserState userState = getUserState(con, kcinfo);
			
			if (userState == null) {
				
				userState = createUserState(con, kcinfo);
				
			} else {
				
				userState = updateUserState(con, userState, kcinfo);
			}
			
			return userState;
		});
	}

	private UserState createUserState(Connection con, KeycloakUserInfo kcinfo) throws SQLException {
		
		PreparedStatement stm = con.prepareStatement("INSERT INTO UserState VALUES (?,?,?,?,?,?)");
		int i = 1;
		stm.setString(i++, kcinfo.subject);
		stm.setString(i++, kcinfo.username);
		stm.setString(i++, kcinfo.email);
		stm.setInt(i++, 1);
		stm.setTimestamp(i++, getLoginTime());
		stm.setString(i++, "Active");
		stm.execute();
		
		return getUserState(con, kcinfo);
	}

	private UserState updateUserState(Connection con, UserState userState, KeycloakUserInfo kcinfo) throws SQLException {
		
		PreparedStatement stm = con.prepareStatement("UPDATE UserState SET username=?,email=?,logins=?,lastLogin=? WHERE userId=?");
		int i = 1;
		stm.setString(i++, kcinfo.username);
		stm.setString(i++, kcinfo.email);
		stm.setInt(i++, userState.getLoginCount() + 1);
		stm.setTimestamp(i++, getLoginTime());
		stm.setString(i++, kcinfo.subject);
		stm.execute();
		
		return getUserState(con, kcinfo);
	}

	private UserState getUserState(Connection con, KeycloakUserInfo kcinfo) throws SQLException {
		UserState userState = null;
		try (PreparedStatement stm = con.prepareStatement("SELECT * FROM UserState WHERE userId=?")) {
			stm.setString(1, kcinfo.subject);
			ResultSet rs = stm.executeQuery();
			if (rs.next()) {
				int loginCount = rs.getInt("logins");
				String status = rs.getString("status");
				Date lastLogin = new Date(rs.getTimestamp("lastLogin").getTime());
				userState = new UserState(kcinfo, loginCount, lastLogin, status);
			}
		}
		return userState;
	}

	private Timestamp getLoginTime() {
		long secs = System.currentTimeMillis() / 1000L;
		return new Timestamp(secs * 1000L);
	}
}
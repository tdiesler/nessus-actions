package io.nessus.actions.jaxrs.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import io.nessus.actions.core.NessusConfig;
import io.nessus.actions.core.utils.ApiUtils;
import io.nessus.actions.jaxrs.type.UserModel;
import io.nessus.actions.jaxrs.type.UserModelAdd;

public class UserModelService extends AbstractJaxrsService {

	public UserModelService(NessusConfig config) {
		super(config);
	}

	public UserModel createModel(UserModelAdd modelAdd) {

		return withConnection(con -> {
			
			String userId = modelAdd.getUserId();
			UserModel userModel = new UserModel(modelAdd)
					.withModelId(ApiUtils.createIdentifier(userId));
			
			PreparedStatement stm = con.prepareStatement("INSERT INTO UserModel VALUES (?,?,?)");
			int i = 1;
			stm.setString(i++, userId);
			stm.setString(i++, userModel.getModelId());
			stm.setString(i++, userModel.getContent());
			stm.execute();
			
			return userModel;
		});
	}

	public UserModel findModel(String modelId) {
		
		return withConnection(con -> {
			
			UserModel userModel = null;
			
			PreparedStatement stm = con.prepareStatement("SELECT * FROM UserModel WHERE modelId=?");
			int i = 1;
			stm.setString(i++, modelId);
			ResultSet rs = stm.executeQuery();
			if (rs.next()) {
				String userId = rs.getString("userId");
				String content = rs.getString("content");
				userModel = new UserModel(modelId, userId, content);
			}
			
			return userModel;
		});
	}

	public List<UserModel> findUserModels(String userId) {

		return withConnection(con -> {
			
			List<UserModel> models = new ArrayList<>();
			
			PreparedStatement stm = con.prepareStatement("SELECT * FROM UserModel WHERE userId=?");
			stm.setString(1, userId);
			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				String modelId = rs.getString("modelId");
				String content = rs.getString("content");
				models.add(new UserModel(modelId, userId, content));
			}
			
			return models;
		});
	}
	
	public UserModel updateModel(UserModel userModel) {
		
		return withConnection(con -> {
			
			PreparedStatement stm = con.prepareStatement("UPDATE UserModel SET content=? WHERE modelId=?");
			int i = 1;
			stm.setString(i++, userModel.getContent());
			stm.setString(i++, userModel.getModelId());
			stm.execute();
			
			return userModel;
		});
	}

	public UserModel deleteModel(String modelId) {
		UserModel userModel = findModel(modelId);
		if (userModel != null) {
			withConnection(con -> {
				PreparedStatement stm = con.prepareStatement("DELETE FROM UserModel WHERE modelId=?");
				stm.setString(1, modelId);
				stm.execute();
				return null;
			});
		}
		return userModel;
	}
}
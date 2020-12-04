package io.nessus.actions.jaxrs.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import io.nessus.actions.core.NessusConfig;
import io.nessus.actions.core.types.MavenBuildHandle.BuildStatus;
import io.nessus.actions.core.utils.ApiUtils;
import io.nessus.actions.jaxrs.type.Model;
import io.nessus.actions.jaxrs.type.Model.ModelState;
import io.nessus.actions.jaxrs.type.Model.TargetRuntime;
import io.nessus.actions.jaxrs.type.ModelAdd;

public class ModelService extends AbstractJaxrsService {

	public ModelService(NessusConfig config) {
		super(config);
	}

	public Model createModel(ModelAdd modelAdd) {

		return withConnection(con -> {
			
			String userId = modelAdd.getUserId();
			Model model = new Model(modelAdd)
					.withModelId(ApiUtils.createIdentifier(userId));
			
			PreparedStatement stm = con.prepareStatement("INSERT INTO UserModel VALUES (?,?,?)");
			int i = 1;
			stm.setString(i++, userId);
			stm.setString(i++, model.getModelId());
			stm.setString(i++, model.getContent());
			stm.execute();
			
			return model;
		});
	}

	public Model findModel(String modelId) {
		
		return withConnection(con -> {
			Model model = findModelInternal(con, modelId);
			return model;
		});
	}

	public List<Model> findModels(String userId) {

		return withConnection(con -> {
			
			List<Model> models = new ArrayList<>();
			
			PreparedStatement stm = con.prepareStatement("SELECT modelId FROM UserModel WHERE userId=?");
			stm.setString(1, userId);
			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				String modelId = rs.getString("modelId");
				models.add(findModelInternal(con, modelId));
			}
			
			return models;
		});
	}
	
	public Model updateModel(Model model) {
		
		return withConnection(con -> {
			
			PreparedStatement stm = con.prepareStatement("UPDATE UserModel SET content=? WHERE modelId=?");
			int i = 1;
			stm.setString(i++, model.getContent());
			stm.setString(i++, model.getModelId());
			stm.execute();
			
			return model;
		});
	}

	public Model updateModelState(Model model, ModelState state) {
		
		String modelId = model.getModelId();
		
		return withConnection(con -> {
			
			PreparedStatement stm = con.prepareStatement("DELETE FROM UserModelState WHERE modelId=? AND runtime=?");
			int i = 1;
			stm.setString(i++, modelId);
			stm.setString(i++, state.getRuntime().toString());
			stm.execute();
			
			stm = con.prepareStatement("INSERT INTO UserModelState VALUES (?,?,?)");
			i = 1;
			stm.setString(i++, modelId);
			stm.setString(i++, state.getRuntime().toString());
			stm.setString(i++, state.getBuildStatus().toString());
			stm.execute();
			
			Model resmodel = findModelInternal(con, modelId);
			
			return resmodel;
		});
	}

	public Model deleteModel(String modelId) {
		Model model = findModel(modelId);
		if (model != null) {
			withConnection(con -> {
				PreparedStatement stm = con.prepareStatement("DELETE FROM UserModel WHERE modelId=?");
				stm.setString(1, modelId);
				stm.execute();
				return null;
			});
		}
		return model;
	}

	private Model findModelInternal(Connection con, String modelId) throws SQLException {
		
		Model model = null;
		
		PreparedStatement stm = con.prepareStatement("SELECT * FROM UserModel WHERE modelId=?");
		int i = 1;
		stm.setString(i++, modelId);
		ResultSet rs = stm.executeQuery();
		if (rs.next()) {
			String userId = rs.getString("userId");
			String content = rs.getString("content");
			List<ModelState> states = findModelStates(con, modelId);
			model = new Model(modelId, userId, content, states);
		}
		
		return model;
	}

	private List<ModelState> findModelStates(Connection con, String modelId) throws SQLException {

		List<ModelState> states = new ArrayList<>();
		
		PreparedStatement stm = con.prepareStatement("SELECT * FROM UserModelState WHERE modelId=? ORDER BY runtime");
		stm.setString(1, modelId);
		ResultSet rs = stm.executeQuery();
		while (rs.next()) {
			TargetRuntime runtime = TargetRuntime.valueOf(rs.getString("runtime"));
			BuildStatus status = BuildStatus.valueOf(rs.getString("status"));
			states.add(new ModelState(runtime, status));
		}
		
		return states;
	}

}
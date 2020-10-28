package io.nessus.actions.jaxrs.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import io.nessus.actions.core.NessusConfig;
import io.nessus.actions.core.service.AbstractService;
import io.nessus.actions.jaxrs.type.UserModel;
import io.nessus.actions.jaxrs.type.UserModelAdd;

public class UserModelsService extends AbstractService<NessusConfig> {

	private final Map<String, List<UserModel>> userModels = new HashMap<>();
	private final AtomicInteger sequence = new AtomicInteger();
	
	public UserModelsService(NessusConfig config) {
		super(config);
	}

	public UserModel insertModel(UserModelAdd modelAdd) {

		Integer nextId = sequence.incrementAndGet();
		UserModel userModel = new UserModel(modelAdd)
				.withModelId(nextId.toString());
		
		List<UserModel> list = getModelList(modelAdd.userId);
		list.add(userModel);
		
		return userModel;
	}

	public UserModel findModel(String userId, String modelId) {
		UserModel model = getModelList(userId).stream()
			.filter(m -> m.modelId.equals(modelId))
			.findAny().orElse(null);
		return model;
	}

	public List<UserModel> findUserModels(String userId) {
		List<UserModel> models = getModelList(userId).stream()
				.map(m -> new UserModel(m).withContent(null))
				.collect(Collectors.toList());
		return models;
	}
	
	public UserModel updateModel(UserModel userModel) {
		
		String userId = userModel.userId;
		String modelId = userModel.modelId;
		
		deleteModel(userId, modelId);
		
		List<UserModel> list = getModelList(userId);
		list.add(userModel);
		
		return userModel;
	}

	public UserModel deleteModel(String userId, String modelId) {
		UserModel userModel = findModel(userId, modelId);
		if (userModel != null) {
			List<UserModel> list = userModels.get(userId);
			list.remove(userModel);
		}
		return userModel;
	}

	private List<UserModel> getModelList(String userId) {
		List<UserModel> list = userModels.get(userId);
		if (list == null) {
			list = new ArrayList<UserModel>();
			userModels.put(userId, list);
		}
		return list;
	}
}
package io.nessus.actions.portal.resources;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import io.nessus.actions.model.utils.AssertState;

@ApplicationPath("/api")
public class ApiRoot extends Application {
	
	private static final Set<Object> singletons = new HashSet<>();
	static {
		singletons.add(new Register());
		singletons.add(new Unregister());
	}
	
	private static ApiRoot INSTANCE;
	
	public ApiRoot() {
		AssertState.isNull(INSTANCE);
		INSTANCE = this;
		
	}
	
	static Register getUserRegistry() {
		return getSingletonResource(Register.class);
	}
	
	@Override
	public Set<Object> getSingletons() {
		return Collections.unmodifiableSet(singletons);
	}

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<>();
		classes.add(Status.class);
		return Collections.unmodifiableSet(classes);
	}

	@SuppressWarnings("unchecked")
	private static <T> T getSingletonResource(Class<T> type) {
		Object res = singletons.stream()
			.filter(obj -> type.isAssignableFrom(obj.getClass()))
			.findAny().get();
		return (T) res;
	}
}
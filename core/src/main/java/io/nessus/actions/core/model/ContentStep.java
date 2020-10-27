package io.nessus.actions.core.model;

public interface ContentStep<S extends Step, C> extends ParameterStep<S> {
	C getContent();
}
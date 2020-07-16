package io.nessus.actions.model.step;

public interface ContentStep<S extends Step, C> extends ParameterStep<S> {
	C getContent();
}
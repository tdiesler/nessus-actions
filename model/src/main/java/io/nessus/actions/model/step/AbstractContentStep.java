package io.nessus.actions.model.step;

import io.nessus.actions.model.step.ParameterStep.ParameterStepContent;

public abstract class AbstractContentStep<S extends Step, C extends ParameterStepContent> implements ContentStep<S, C> {

	final ParameterStepContent content;
	
	public AbstractContentStep(C content) {
		this.content = content;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public S withParams(String spec) {
		content.withParams(spec);
		return (S) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public S withParam(String key, Object value) {
		content.withParam(key, value);
		return (S) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public C getContent() {
		return (C) content;
	}
}
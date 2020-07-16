package io.nessus.actions.model;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.nessus.actions.model.step.AbstractContentStep;
import io.nessus.actions.model.step.ComponentStep;
import io.nessus.actions.model.step.ComponentStep.ComponentStepContent;
import io.nessus.actions.model.utils.Parameters;

public class ToStep extends AbstractContentStep<ToStep, ComponentStepContent> implements ComponentStep<ToStep> {

	@ConstructorProperties({"comp", "with"})
	public ToStep(String comp, String with) {
		super(new ComponentStepContent(comp, with));
	}
	
	@JsonGetter("to")
	public ComponentStepContent getContent() {
		return super.getContent();
	}

	@Override
	@JsonIgnore
	public String getComp() {
		return getContent().getComp();
	}

	@Override
	@JsonIgnore
	public String getWith() {
		return getContent().getWith();
	}
	
	@Override
	@JsonIgnore
	public Parameters getParameters() {
		return getContent().getParameters();
	}

}
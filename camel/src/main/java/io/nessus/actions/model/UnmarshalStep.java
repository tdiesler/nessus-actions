package io.nessus.actions.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.nessus.actions.model.UnmarshalStep.UnmarshalStepContent;
import io.nessus.actions.model.step.AbstractContentStep;
import io.nessus.actions.model.step.ParameterStep.ParameterStepContent;
import io.nessus.actions.model.utils.Parameters;

public class UnmarshalStep extends AbstractContentStep<UnmarshalStep, UnmarshalStepContent> {
	
	public UnmarshalStep(String format) {
		super(new UnmarshalStepContent(format));
	}

	@JsonGetter("unmarshal")
	public UnmarshalStepContent getContent() {
		return super.getContent();
	}

	@Override
	@JsonIgnore
	public Parameters getParameters() {
		return getContent().getParameters();
	}
	
	public static class UnmarshalStepContent extends ParameterStepContent {
		
		final String format;

		public UnmarshalStepContent(String format) {
			this.format = format;
		}

		public String getFormat() {
			return format;
		}
	}
}
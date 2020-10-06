package io.nessus.actions.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.nessus.actions.model.MarshalStep.MarshalStepContent;
import io.nessus.actions.model.step.AbstractContentStep;
import io.nessus.actions.model.step.ParameterStep.ParameterStepContent;
import io.nessus.common.Parameters;

public class MarshalStep extends AbstractContentStep<MarshalStep, MarshalStepContent> {
	
	public MarshalStep(String format, boolean pretty) {
		super(new MarshalStepContent(format, pretty));
	}

	@JsonGetter("marshal")
	public MarshalStepContent getContent() {
		return super.getContent();
	}

	@Override
	@JsonIgnore
	public Parameters getParameters() {
		return getContent().getParameters();
	}

	public static class MarshalStepContent extends ParameterStepContent {
		
		final String format;
		final boolean pretty;

		public MarshalStepContent(String format, boolean pretty) {
			this.format = format;
			this.pretty = pretty;
		}

		public String getFormat() {
			return format;
		}

		public boolean isPretty() {
			return pretty;
		}
	}
}
package io.nessus.actions.model.step;

import java.util.Map.Entry;

public interface ComponentStep<S extends Step> extends ParameterStep<S> {
	
	String getComp();
	String getWith();
	
	default String toCamelUri() {
		String name = ComponentGAV.fromString(getComp()).getName();
		String uri = String.format("%s:%s", name, getWith());
		int index = 0;
		for (Entry<String, Object> en : getParameters().toMap().entrySet()) {
			uri += (++index == 1 ? "?" : "&");
			uri += en.getKey() + "=" + en.getValue();
		}
		return uri;
	}

	static class ComponentStepContent extends ParameterStepContent {
		
		final String comp;
		final String with;
		
		public ComponentStepContent(String comp, String with) {
			this.comp = comp;
			this.with = with;
		}

		public String getComp() {
			return comp;
		}

		public String getWith() {
			return with;
		}
	}
}
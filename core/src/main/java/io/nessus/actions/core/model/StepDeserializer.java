package io.nessus.actions.core.model;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import io.nessus.common.AssertState;

@SuppressWarnings("serial") 
public class StepDeserializer extends StdDeserializer<Step> { 
 	 
    public StepDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
    public Step deserialize(JsonParser jp, DeserializationContext ctxt) 
      throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        String first = node.fieldNames().next();
        Step step = null;
        if (first.equals("to")) {
        	String comp = node.at(String.format("/%s/comp", first)).asText();
        	String with = node.at(String.format("/%s/with", first)).asText();
        	step = new ToStep(comp, with);
        }
        else if (first.equals("marshal")) {
        	String name = node.at(String.format("/%s/format", first)).asText();
        	Boolean pretty = node.at(String.format("/%s/pretty", first)).asBoolean();
        	step = new MarshalStep(name, pretty);
        }
        else if (first.equals("unmarshal")) {
        	String name = node.at(String.format("/%s/format", first)).asText();
        	step = new UnmarshalStep(name);
        }
        AssertState.notNull(step, "Unsupported step: " + node);
        JsonNode pnode = node.at(String.format("/%s/params", first));
        if (!pnode.isMissingNode()) {
        	ParameterStep<?> pstep = (ParameterStep<?>) step;
        	pnode.fieldNames().forEachRemaining(key -> {
        		String value = pnode.get(key).asText();
        		pstep.withParam(key, value);
        	});
        }
        return step;
    }
}
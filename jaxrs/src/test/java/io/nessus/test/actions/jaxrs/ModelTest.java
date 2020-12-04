/*-
 * #%L
 * Nessus :: Weka :: API
 * %%
 * Copyright (C) 2020 Nessus
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.nessus.test.actions.jaxrs;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import io.nessus.actions.core.types.MavenBuildHandle.BuildStatus;
import io.nessus.actions.jaxrs.type.Model;
import io.nessus.actions.jaxrs.type.Model.ModelState;
import io.nessus.actions.jaxrs.type.Model.TargetRuntime;
import io.nessus.common.utils.StreamUtils;

public class ModelTest extends AbstractJaxrsTest {

	@Test
	public void testConfig() throws Exception {

		InputStream input = getClass().getResourceAsStream("/model/crypto-ticker.yaml");
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		StreamUtils.copyStream(input, output);

		String content = new String(output.toByteArray());
		List<ModelState> states = Arrays.asList(new ModelState(TargetRuntime.standalone, BuildStatus.Success));
		Model exp = new Model("model01", "user01", content, states);
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
		String expstr = writer.writeValueAsString(exp);
		logInfo("{}", expstr);
		
		Model was = mapper.readValue(expstr, Model.class);
		String wasstr = writer.writeValueAsString(was);
		logInfo("{}", wasstr);
		
		Assert.assertEquals(expstr, wasstr);
	}
}
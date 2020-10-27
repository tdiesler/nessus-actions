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

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import io.nessus.actions.core.utils.IOUtils;
import io.nessus.actions.jaxrs.type.UserModel;

public class UserModelTest extends AbstractJaxrsTest {

	@Test
	public void testConfig() throws Exception {

		InputStream input = getClass().getResourceAsStream("/model/crypto-ticker.yaml");
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		IOUtils.copyStream(input, output);

		String content = new String(output.toByteArray());
		UserModel exp = new UserModel("model01", "user01", "Some Title", content);
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
		String expstr = writer.writeValueAsString(exp);
		logInfo("{}", expstr);
		
		UserModel was = mapper.readValue(expstr, UserModel.class);
		String wasstr = writer.writeValueAsString(was);
		logInfo("{}", wasstr);
		
		Assert.assertEquals(expstr, wasstr);
	}
}
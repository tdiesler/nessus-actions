/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.acme.test.ticker;

import java.util.Map;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;

/**
 * JVM mode tests.
 */
@QuarkusTest
public class TickerTest {

	static final Logger LOG = LoggerFactory.getLogger(TickerTest.class);
	
    @Test
    public void btcusdt() {

    	Response res = ClientBuilder.newClient().target(getTargetUri("/xchange"))
    			.request().get();
    	
    	Assert.assertEquals(200, res.getStatus());
    	
    	@SuppressWarnings("unchecked")
		Map<String, Object> resmap = res.readEntity(Map.class);
		LOG.info("{}", resmap);
		
    	Assert.assertEquals("BTC/USDT", resmap.get("currencyPair"));
    	Assert.assertNotNull(resmap.get("last"));
    }

	private String getTargetUri(String path) {
		return String.format("http://localhost:%d%s", RestAssured.port, path);
	}
}

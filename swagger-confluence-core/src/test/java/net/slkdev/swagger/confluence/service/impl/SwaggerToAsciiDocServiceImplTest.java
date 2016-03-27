/*
 * Copyright 2016 Aaron Knight
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.slkdev.swagger.confluence.service.impl;

import net.slkdev.swagger.confluence.service.SwaggerToAsciiDocService;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class SwaggerToAsciiDocServiceImplTest {

	private SwaggerToAsciiDocService swaggerToAsciiDocService;

	@Before
	public void setUp(){
		swaggerToAsciiDocService = new SwaggerToAsciiDocServiceImpl();
	}

	@Test
	public void testSwaggerToAsciiDocConversionWithValidYamlIsSuccessful() throws IOException {
		final String asciiDoc = swaggerToAsciiDocService.convertSwaggerToAsciiDoc("/swagger-petstore-example.yaml");

		// Note: Can't compare against the resource file as the upstream library
		// is not order deterministic - it will intermittently fail. Since this
		// initial stage is wrapping an upstream library in a fairly basic manner,
		// I will assume that its doing its job if I get an response for now.
		assertNotNull("Swagger Ascii Doc Should Not Be Null!", asciiDoc);
	}

	@Test(expected=RuntimeException.class)
	public void testSwaggerToAsciiDocConversionWithInvalidFileThrowsException(){
		swaggerToAsciiDocService.convertSwaggerToAsciiDoc("/non-existant.yaml");
	}

}

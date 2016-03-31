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

import net.slkdev.swagger.confluence.service.AsciiDocToXHtmlService;
import org.asciidoctor.internal.IOUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class AsciiDocToXHtmlServiceImplTest {

	private AsciiDocToXHtmlService asciiDocToXHtmlService;

	@Before
	public void setUp(){
		asciiDocToXHtmlService = new AsciiDocToXHtmlServiceImpl();
	}

	@Test
	public void testAsciiDocToXHtmlConversion(){
		final String asciiDoc = IOUtils.readFull(
				AsciiDocToXHtmlServiceImplTest.class.getResourceAsStream(
						"/swagger-petstore-asciidoc-example.adoc")
		);

		final String xhtml = asciiDocToXHtmlService.convertAsciiDocToXHtml(asciiDoc);

		assertNotNull("XHtml Output Should Not Be Null!", xhtml);

	}
}

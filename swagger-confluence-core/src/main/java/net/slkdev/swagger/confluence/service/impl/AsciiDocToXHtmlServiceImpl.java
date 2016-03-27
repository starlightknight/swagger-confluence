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
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.AttributesBuilder;
import org.asciidoctor.OptionsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.asciidoctor.Asciidoctor.Factory.create;

public class AsciiDocToXHtmlServiceImpl implements AsciiDocToXHtmlService {

	private static final Logger LOG = LoggerFactory.getLogger(AsciiDocToXHtmlServiceImpl.class);

	public String convertAsciiDocToXHtml(final String asciiDoc){
		LOG.info("Converting AsciiDoc to XHTML5...");

		final Asciidoctor asciidoctor = create();

		final Map<String, Object> attributes = AttributesBuilder.attributes()
				.unsetStyleSheet()
				.asMap();

		final Map<String, Object> options = OptionsBuilder.options()
				.attributes(attributes)
				.backend("xhtml5")
				.asMap();

		LOG.info("XHTML5 Conversion Complete!");

		return asciidoctor.render(asciiDoc, options);
	}

}

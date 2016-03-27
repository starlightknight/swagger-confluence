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

import io.github.robwin.markup.builder.MarkupLanguage;
import io.github.robwin.swagger2markup.Swagger2MarkupConverter;
import net.slkdev.swagger.confluence.service.SwaggerToAsciiDocService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class SwaggerToAsciiDocServiceImpl implements SwaggerToAsciiDocService {

	private static final Logger LOG = LoggerFactory.getLogger(SwaggerToAsciiDocServiceImpl.class);

	public String convertSwaggerToAsciiDoc(final String swaggerSchemaPath) {
		final File swaggerSchemaFile;

		LOG.info("Converting Swagger Schema to Ascii Doc...");

		try {
			swaggerSchemaFile = getSchemaFile(swaggerSchemaPath);
		}
		catch(final FileNotFoundException | URISyntaxException e){
			throw new RuntimeException("Error Locating Swagger Schema", e);
		}

		final String swaggerAsciiDoc;

		try {
			swaggerAsciiDoc = Swagger2MarkupConverter.from(swaggerSchemaFile.getAbsolutePath())
					.withMarkupLanguage(MarkupLanguage.ASCIIDOC)
					.build()
					.asString();
		}
		catch(IOException e){
			throw new RuntimeException("Error Converting Swagger Schema to AsciiDoc", e);
		}

		LOG.info("AsciiDoc Conversion Complete!");

		return swaggerAsciiDoc;
	}

	private File getSchemaFile(final String swaggerSchemaPath) throws FileNotFoundException, URISyntaxException {
		// First we'll try to find the file directly
		File swaggerFile = new File(swaggerSchemaPath);

		// If we can't find it, we'll check the classpath
		if(!swaggerFile.exists()){
			final URL swaggerSchemaURL = SwaggerToAsciiDocServiceImpl.class.getResource(swaggerSchemaPath);

			if(swaggerSchemaURL == null) {
				swaggerFile = null;
			}
			else {
				swaggerFile = new File(swaggerSchemaURL.toURI());
			}
		}

		if(swaggerFile == null || !swaggerFile.exists() || !swaggerFile.canRead()){
			throw new FileNotFoundException(
					String.format("Unable to Locate Swagger Schema at Path <%s>",
							swaggerSchemaPath));
		}

		return swaggerFile;
	}

}

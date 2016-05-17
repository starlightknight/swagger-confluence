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

import io.github.swagger2markup.*;
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder;
import io.github.swagger2markup.markup.builder.MarkupLanguage;
import net.slkdev.swagger.confluence.exception.SwaggerConfluenceConfigurationException;
import net.slkdev.swagger.confluence.exception.SwaggerConfluenceInternalSystemException;
import net.slkdev.swagger.confluence.service.SwaggerToAsciiDocService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SwaggerToAsciiDocServiceImpl implements SwaggerToAsciiDocService {

    private static final Logger LOG = LoggerFactory.getLogger(SwaggerToAsciiDocServiceImpl.class);

    @Override
    public String convertSwaggerToAsciiDoc(final String swaggerSchemaPath) {
        final File swaggerSchemaFile;

        LOG.info("Converting Swagger Schema to Ascii Doc...");

        try {
            swaggerSchemaFile = getSchemaFile(swaggerSchemaPath);
        } catch (final FileNotFoundException | URISyntaxException e) {
            throw new SwaggerConfluenceConfigurationException("Error Locating Swagger Schema", e);
        }


        final String swaggerAsciiDoc;

        try {
            final Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
                    .withMarkupLanguage(MarkupLanguage.ASCIIDOC)
                    .withOutputLanguage(Language.EN)
                    .withPathsGroupedBy(GroupBy.AS_IS)
                    .withOperationOrdering(OrderBy.AS_IS)
                    .build();

            final String swaggerSchema = FileUtils.readFileToString(swaggerSchemaFile, StandardCharsets.UTF_8);

            swaggerAsciiDoc = Swagger2MarkupConverter.from(swaggerSchema)
                    .withConfig(config)
                    .build()
                    .toString();

        } catch (IOException e) {
            throw new SwaggerConfluenceInternalSystemException(
                    "Error Converting Swagger Schema to AsciiDoc", e);
        }

        LOG.info("AsciiDoc Conversion Complete!");

        return swaggerAsciiDoc;
    }

    private static File getSchemaFile(final String swaggerSchemaPath) throws FileNotFoundException, URISyntaxException {
        // First we'll try to find the file directly
        File swaggerFile = new File(swaggerSchemaPath);

        // If we can't find it, we'll check the classpath
        if (!swaggerFile.exists()) {
            final URL swaggerSchemaURL = SwaggerToAsciiDocServiceImpl.class.getResource(swaggerSchemaPath);

            if (swaggerSchemaURL == null) {
                swaggerFile = null;
            } else {
                swaggerFile = new File(swaggerSchemaURL.toURI());
            }
        }

        if (swaggerFile == null || !swaggerFile.exists() || !swaggerFile.canRead()) {
            throw new FileNotFoundException(
                    String.format("Unable to Locate Swagger Schema at Path <%s>",
                            swaggerSchemaPath));
        }

        return swaggerFile;
    }

}

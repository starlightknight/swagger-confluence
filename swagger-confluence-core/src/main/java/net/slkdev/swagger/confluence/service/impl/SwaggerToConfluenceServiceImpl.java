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

import net.slkdev.swagger.confluence.config.SwaggerConfluenceConfig;
import net.slkdev.swagger.confluence.constants.PaginationMode;
import net.slkdev.swagger.confluence.service.AsciiDocToXHtmlService;
import net.slkdev.swagger.confluence.service.SwaggerToAsciiDocService;
import net.slkdev.swagger.confluence.service.SwaggerToConfluenceService;
import net.slkdev.swagger.confluence.service.XHtmlToConfluenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.Validate.notNull;

public class SwaggerToConfluenceServiceImpl implements SwaggerToConfluenceService {

	private static final Logger LOG = LoggerFactory.getLogger(SwaggerToConfluenceServiceImpl.class);

	private final SwaggerToAsciiDocService swaggerToAsciiDocService;
	private final AsciiDocToXHtmlService asciiDocToXHtmlService;
	private final XHtmlToConfluenceService xHtmlToConfluenceService;

	public SwaggerToConfluenceServiceImpl(final SwaggerToAsciiDocService swaggerToAsciiDocService,
	                                      final AsciiDocToXHtmlService asciiDocToXHtmlService,
	                                      final XHtmlToConfluenceService xHtmlToConfluenceService){
		this.swaggerToAsciiDocService = swaggerToAsciiDocService;
		this.asciiDocToXHtmlService = asciiDocToXHtmlService;
		this.xHtmlToConfluenceService = xHtmlToConfluenceService;
	}

	public void convertSwaggerToConfluence(final SwaggerConfluenceConfig swaggerConfluenceConfig){
		final String swaggerSchema = swaggerConfluenceConfig.getSwaggerSchema();
		final String confluenceRestApiUrl = swaggerConfluenceConfig.getConfluenceRestApiUrl();
		final String spaceKey = swaggerConfluenceConfig.getSpaceKey();
		final String prefix = swaggerConfluenceConfig.getPrefix();
		final PaginationMode paginationMode = swaggerConfluenceConfig.getPaginationMode();

		notNull("Swagger Schema Cannot Be Null!", swaggerSchema);
		notNull("Confluence REST API URL Cannot Be Null!", confluenceRestApiUrl);
		notNull("Confluence Authentication Cannot Be Null!", swaggerConfluenceConfig.getAuthentication());
		notNull("Confluence Space Key Cannot Be Null!", spaceKey);

		LOG.info("Publishing Swagger API Documentation to Confluence...");
		LOG.info("Swagger Schema: {}", swaggerSchema);
		LOG.info("Confluence REST API URL: {}", confluenceRestApiUrl);
		LOG.info("Confluence Space Key: {}", spaceKey);
		LOG.info("Confluence PaginationMode: {}", paginationMode);

		if(prefix == null){
			LOG.info("Confluence Title Prefix: No Prefix Supplied");
		}
		else {
			LOG.info("Confluence Title Prefix: {}", prefix);
		}

		final String asciiDoc = swaggerToAsciiDocService.convertSwaggerToAsciiDoc(swaggerSchema);
		final String xhtml = asciiDocToXHtmlService.convertAsciiDocToXHtml(asciiDoc);
		xHtmlToConfluenceService.postXHtmlToConfluence(swaggerConfluenceConfig, xhtml);
	}
	
}

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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SwaggerToConfluenceImplTest {

    @Mock
    private AsciiDocToXHtmlService asciiDocToXHtmlService;

    @Mock
    private SwaggerToAsciiDocService swaggerToAsciiDocService;

    @Mock
    private XHtmlToConfluenceService xHtmlToConfluenceService;

    private SwaggerConfluenceConfig swaggerConfluenceConfig;
    private SwaggerToConfluenceService swaggerToConfluenceService;

    @Before
    public void setUp(){
        swaggerToConfluenceService = new SwaggerToConfluenceServiceImpl(
                swaggerToAsciiDocService, asciiDocToXHtmlService, xHtmlToConfluenceService);
        swaggerConfluenceConfig = new SwaggerConfluenceConfig();
        swaggerConfluenceConfig.setAncestorId(1);
        swaggerConfluenceConfig.setIncludeTableOfContentsOnSinglePage(true);
        swaggerConfluenceConfig.setSpaceKey("TEST");
        swaggerConfluenceConfig.setSwaggerSchema("test.yaml");
        swaggerConfluenceConfig.setPrefix("[TEST]");
        swaggerConfluenceConfig.setAuthentication("abcdef");
        swaggerConfluenceConfig.setConfluenceRestApiUrl("http://localhost/rest/api");
        swaggerConfluenceConfig.setGenerateNumericPrefixes(true);
        swaggerConfluenceConfig.setPaginationMode(PaginationMode.SINGLE_PAGE);
        swaggerConfluenceConfig.setTitle("Test");
    }

    @Test(expected=NullPointerException.class)
    public void testSwaggerSchemaCannotBeNull(){
        swaggerConfluenceConfig.setSwaggerSchema(null);
        swaggerToConfluenceService.convertSwaggerToConfluence(swaggerConfluenceConfig);
    }

    @Test(expected=NullPointerException.class)
    public void testConfluenceRestAPIUrlCannotBeNull(){
        swaggerConfluenceConfig.setConfluenceRestApiUrl(null);
        swaggerToConfluenceService.convertSwaggerToConfluence(swaggerConfluenceConfig);
    }

    @Test(expected=NullPointerException.class)
    public void testAuthenticationCannotBeNull(){
        swaggerConfluenceConfig.setAuthentication(null);
        swaggerToConfluenceService.convertSwaggerToConfluence(swaggerConfluenceConfig);
    }

    @Test(expected=NullPointerException.class)
    public void testSpaceKeyCannotBeNull(){
        swaggerConfluenceConfig.setSpaceKey(null);
        swaggerToConfluenceService.convertSwaggerToConfluence(swaggerConfluenceConfig);
    }
}

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
package net.slkdev.swagger.confluence.cli;

import net.slkdev.swagger.confluence.config.SwaggerConfluenceConfig;
import net.slkdev.swagger.confluence.constants.PaginationMode;
import net.slkdev.swagger.confluence.service.SwaggerToConfluenceService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SwaggerConfluenceTest {

    @Mock
    private SwaggerToConfluenceService swaggerToConfluenceService;

    private SwaggerConfluence swaggerConfluence;

    @Before
    public void setUp(){
        swaggerConfluence = new SwaggerConfluence(swaggerToConfluenceService);
    }

    @Test
    public void testRunCLI(){
        final String[] args = new String[]{
                "-b", "c3RhcmxpZ2h0a25pZ2h0OnRyb2wsb2NrNw==",
                "-g", "false", "-p", "[CLI]", "-i", "false",
                "-k", "DOC", "-s", "swagger-petstore-example.yaml",
                "-t", "Swagger Pet Store", "-m", "single"
        };

        final ArgumentCaptor<SwaggerConfluenceConfig> swaggerConfluenceConfigArgumentCaptor =
                ArgumentCaptor.forClass(SwaggerConfluenceConfig.class);

        swaggerConfluence.runCLI(args);

        verify(swaggerToConfluenceService).convertSwaggerToConfluence(
                swaggerConfluenceConfigArgumentCaptor.capture());

        final SwaggerConfluenceConfig swaggerConfluenceConfig =
                swaggerConfluenceConfigArgumentCaptor.getValue();

        assertEquals("Authentication Parsed Incorrectly!", args[1],
                swaggerConfluenceConfig.getAuthentication());
        assertEquals("Generate Numeric Prefixes Parsed Incorrectly!", Boolean.valueOf(args[3]),
                swaggerConfluenceConfig.isGenerateNumericPrefixes());
        assertEquals("Prefix Parsed Incorrectly!", args[5]+" ",
                swaggerConfluenceConfig.getPrefix());
        assertEquals("Include ToC for Single Parsed Incorrectly!", Boolean.valueOf(args[7]),
                swaggerConfluenceConfig.isIncludeTableOfContentsOnSinglePage());
        assertEquals("Space Key Parsed Incorrectly!", args[9],
                swaggerConfluenceConfig.getSpaceKey());
        assertEquals("Schema Location Parsed Incorrectly!", args[11],
                swaggerConfluenceConfig.getSwaggerSchema());
        assertEquals("Title Parsed Incorrectly!", args[13],
                swaggerConfluenceConfig.getTitle());
        assertEquals("Pagination Mode Parsed Incorrectly!", PaginationMode.SINGLE_PAGE,
                swaggerConfluenceConfig.getPaginationMode());
    }

}

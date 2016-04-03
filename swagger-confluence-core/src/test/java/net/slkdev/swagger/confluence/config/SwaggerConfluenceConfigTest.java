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
package net.slkdev.swagger.confluence.config;

import net.slkdev.swagger.confluence.constants.PaginationMode;
import net.slkdev.swagger.confluence.exception.SwaggerConfluenceConfigurationException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SwaggerConfluenceConfigTest {

    private SwaggerConfluenceConfig swaggerConfluenceConfig;

    @Before
    public void setUp(){
        swaggerConfluenceConfig = new SwaggerConfluenceConfig();
    }

    @Test
    public void testNoDefaultAncestorIdExists(){
        assertNull("No Default Should Be Set", swaggerConfluenceConfig.getAncestorId());
    }

    @Test
    public void testGetAndSetAncestorId(){
        swaggerConfluenceConfig.setAncestorId(1);;
        assertEquals("Expected AncestorId 1", Integer.valueOf(1),
                swaggerConfluenceConfig.getAncestorId());
    }

    @Test
    public void testNoDefaultAuthenticationExists(){
        assertNull("No Default Should Be Set", swaggerConfluenceConfig.getAuthentication());
    }

    @Test
    public void testGetAndSetAuthentication(){
        swaggerConfluenceConfig.setAuthentication("abc");
        assertEquals("Expected Authentication \"abc\"", "abc",
                swaggerConfluenceConfig.getAuthentication());
    }

    @Test
    public void testNoDefaultConfluenceRestApiUrlExists(){
        assertNull("No Default Should Be Set", swaggerConfluenceConfig.getConfluenceRestApiUrl());
    }

    @Test
    public void testGetAndSetConfluenceRestApiUrl(){
        swaggerConfluenceConfig.setConfluenceRestApiUrl("http://localhost");
        assertEquals("Expected Confluence Rest Api \"http://localhost\"", "http://localhost",
                swaggerConfluenceConfig.getConfluenceRestApiUrl());
    }

    @Test
    public void testDefaultGenerateNumericPrefixes(){
        assertTrue("Default Should \"true\"", swaggerConfluenceConfig.isGenerateNumericPrefixes());
    }

    @Test
    public void testGetAndSetGenerateNumericPrefixes(){
        swaggerConfluenceConfig.setGenerateNumericPrefixes(false);
        assertFalse("Expected Generate Numeric Prefixes -> False",
                swaggerConfluenceConfig.isGenerateNumericPrefixes());
    }

    @Test
    public void testDefaultPaginationModeIsSingle(){
        assertEquals("Default Should \"SINGLE_PAGE\"", PaginationMode.SINGLE_PAGE,
                swaggerConfluenceConfig.getPaginationMode());
    }

    @Test
    public void testEnumPaginationModeWithSingle(){
        swaggerConfluenceConfig.setPaginationMode(PaginationMode.SINGLE_PAGE);
        assertEquals("Expected Pagination Mode \"SINGLE_PAGE\"", PaginationMode.SINGLE_PAGE,
                swaggerConfluenceConfig.getPaginationMode());
    }

    @Test
    public void testEnumPaginationModeWithCategory(){
        swaggerConfluenceConfig.setPaginationMode(PaginationMode.CATEGORY_PAGES);
        assertEquals("Expected Pagination Mode \"CATEGORY_PAGES\"",
                PaginationMode.CATEGORY_PAGES, swaggerConfluenceConfig.getPaginationMode());
    }

    @Test
    public void testEnumPaginationModeWithIndividual(){
        swaggerConfluenceConfig.setPaginationMode(PaginationMode.INDIVIDUAL_PAGES);
        assertEquals("Expected Pagination Mode \"INDIVIDUAL_PAGES\"",
                PaginationMode.INDIVIDUAL_PAGES, swaggerConfluenceConfig.getPaginationMode());
    }

    @Test
    public void testStringPaginationModeWithSingle(){
        swaggerConfluenceConfig.setPaginationMode("single");
        assertEquals("Expected Pagination Mode \"SINGLE_PAGE\"", PaginationMode.SINGLE_PAGE,
                swaggerConfluenceConfig.getPaginationMode());
    }

    @Test
    public void testStringPaginationModeWithCategory(){
        swaggerConfluenceConfig.setPaginationMode("category");
        assertEquals("Expected Pagination Mode \"CATEGORY_PAGES\"",
                PaginationMode.CATEGORY_PAGES, swaggerConfluenceConfig.getPaginationMode());
    }

    @Test
    public void testStringPaginationModeWithIndividual(){
        swaggerConfluenceConfig.setPaginationMode("individual");
        assertEquals("Expected Pagination Mode \"INDIVIDUAL_PAGES\"",
                PaginationMode.INDIVIDUAL_PAGES, swaggerConfluenceConfig.getPaginationMode());
    }

    @Test(expected = SwaggerConfluenceConfigurationException.class)
    public void testStringPaginationWithInvalidValueThrowsException(){
        swaggerConfluenceConfig.setPaginationMode("invalid");
    }

    @Test
    public void testNoDefaultPrefixExists(){
        assertNull("No Default Should Be Set", swaggerConfluenceConfig.getPrefix());
    }

    @Test
    public void testPrefixWithNoTrailingSpaceAutomaticallyAddsOne(){
        swaggerConfluenceConfig.setPrefix("[P]");
        assertEquals("Expected Prefix \"[P] \"",
                "[P] ", swaggerConfluenceConfig.getPrefix());
    }

    @Test
    public void testPrefixWithTrailingSpaceDoesntAddAnotherOne(){
        swaggerConfluenceConfig.setPrefix("[P] ");
        assertEquals("Expected Prefix \"[P] \"",
                "[P] ", swaggerConfluenceConfig.getPrefix());
    }

    @Test
    public void testNoDefaultSpaceKeyExists(){
        assertNull("No Default Should Be Set", swaggerConfluenceConfig.getSpaceKey());
    }

    @Test
    public void testGetSetSpaceKey(){
        swaggerConfluenceConfig.setSpaceKey("DOC");
        assertEquals("Expected Space Key \"DOC\"",
                "DOC", swaggerConfluenceConfig.getSpaceKey());
    }

    @Test
    public void testNoDefaultSwaggerSchemaExists(){
        assertNull("No Default Should Be Set", swaggerConfluenceConfig.getSwaggerSchema());
    }

    @Test
    public void testGetSetSwaggerSchema(){
        swaggerConfluenceConfig.setSwaggerSchema("schema.yaml");
        assertEquals("Expected Swagger Schema \"schema.yaml\"",
                "schema.yaml", swaggerConfluenceConfig.getSwaggerSchema());
    }

    @Test
    public void testNoDefaultTitleExists(){
        assertNull("No Default Should Be Set", swaggerConfluenceConfig.getSwaggerSchema());
    }

    @Test
    public void testGetSetTitle(){
        swaggerConfluenceConfig.setTitle("Test Schema");
        assertEquals("Expected Title \"Test Schema\"",
                "Test Schema", swaggerConfluenceConfig.getTitle());
    }

}

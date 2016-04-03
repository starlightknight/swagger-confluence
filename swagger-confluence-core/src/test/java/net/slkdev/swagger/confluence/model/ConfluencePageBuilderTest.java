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
package net.slkdev.swagger.confluence.model;

import net.slkdev.swagger.confluence.constants.PageType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfluencePageBuilderTest {

    private static final Integer ANCESTOR_ID = 1;
    private static final String CONFLUENCE_TITLE = "Confluence Title";
    private static final Boolean EXISTS = true;
    private static final String ID = "1";
    private static final String ORIGINAL_TITLE = "Original Title";
    private static final PageType PAGE_TYPE = PageType.ROOT;
    private static final Integer VERSION = 1;
    private static final String XHTML = "<html></html>";

    @Test
    public void testBuilderWithAllFields(){
        final ConfluencePage confluencePage = ConfluencePageBuilder.aConfluencePage()
                .withConfluenceTitle(CONFLUENCE_TITLE)
                .withPageType(PAGE_TYPE)
                .withAncestorId(ANCESTOR_ID)
                .withExists(EXISTS)
                .withId(ID)
                .withOriginalTitle(ORIGINAL_TITLE)
                .withVersion(VERSION)
                .withXhtml(XHTML)
                .build();

        assertEquals("Ancestor Id Doesn't Match!", ANCESTOR_ID,
                confluencePage.getAncestorId());
        assertEquals("Confluence Title Doesn't Match!", CONFLUENCE_TITLE,
                confluencePage.getConfluenceTitle());
        assertEquals("Exists Doesn't Match!", EXISTS,
                confluencePage.exists());
        assertEquals("Id Doesn't Match!", ID, confluencePage.getId());
        assertEquals("Original Title Doesn't Match!", ORIGINAL_TITLE,
                confluencePage.getOriginalTitle());
        assertEquals("Version Doesn't Match!", VERSION,
                confluencePage.getVersion());
        assertEquals("XHTML Doesn't Match!", XHTML,
                confluencePage.getXhtml());
    }
}

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

public class ConfluenceLinkBuilderTest {

    private static final String CONFLUENCE_LINK_MARKUP = "<ac:link></ac:link>";
    private static final String ORIGINAL_HREF = "#Root";
    private static final PageType PAGE_TYPE = PageType.ROOT;
    private static final String TEXT = "Root";

    @Test
    public void testBuilderWithAllFields(){
        final ConfluenceLink confluenceLink = ConfluenceLinkBuilder.aConfluenceLink()
                .withConfluenceLinkMarkup(CONFLUENCE_LINK_MARKUP)
                .withOriginalHref(ORIGINAL_HREF)
                .withPageType(PAGE_TYPE)
                .withText(TEXT)
                .build();

        assertEquals("Confluence Link Markup Doesn't Match!", CONFLUENCE_LINK_MARKUP,
                confluenceLink.getConfluenceLinkMarkup());
        assertEquals("Original Href Doesn't Match!", ORIGINAL_HREF,
                confluenceLink.getOriginalHref());
        assertEquals("Page Type Doesn't Match!", PAGE_TYPE,
                confluenceLink.getPageType());
        assertEquals("Text Doesn't Match!", TEXT,
                confluenceLink.getText());
    }
}

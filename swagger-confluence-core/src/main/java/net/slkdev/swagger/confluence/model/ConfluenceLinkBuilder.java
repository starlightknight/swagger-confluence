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

public class ConfluenceLinkBuilder {
    private PageType pageType;
    private String originalHref;
    private String confluenceLinkMarkup;
    private String text;

    private ConfluenceLinkBuilder() {
    }

    public static ConfluenceLinkBuilder aConfluenceLink() {
        return new ConfluenceLinkBuilder();
    }

    public ConfluenceLinkBuilder withPageType(PageType pageType) {
        this.pageType = pageType;
        return this;
    }

    public ConfluenceLinkBuilder withOriginalHref(String originalHref) {
        this.originalHref = originalHref;
        return this;
    }

    public ConfluenceLinkBuilder withConfluenceLinkMarkup(String confluenceLinkMarkup) {
        this.confluenceLinkMarkup = confluenceLinkMarkup;
        return this;
    }

    public ConfluenceLinkBuilder withText(String text) {
        this.text = text;
        return this;
    }

    public ConfluenceLinkBuilder but() {
        return aConfluenceLink().withPageType(pageType).withOriginalHref(originalHref).withConfluenceLinkMarkup(confluenceLinkMarkup).withText(text);
    }

    public ConfluenceLink build() {
        ConfluenceLink confluenceLink = new ConfluenceLink();
        confluenceLink.setPageType(pageType);
        confluenceLink.setOriginalHref(originalHref);
        confluenceLink.setConfluenceLinkMarkup(confluenceLinkMarkup);
        confluenceLink.setText(text);
        return confluenceLink;
    }
}

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

public class ConfluencePage {

    private Integer ancestorId;
    private String confluenceTitle;
    private Boolean exists;
    private String id;
    private String originalTitle;
    private PageType pageType;
    private Integer version;
    private String xhtml;

    public Integer getAncestorId() {
        return ancestorId;
    }

    public void setAncestorId(Integer ancestorId) {
        this.ancestorId = ancestorId;
    }

    public String getConfluenceTitle() {
        return confluenceTitle;
    }

    public void setConfluenceTitle(String confluenceTitle) {
        this.confluenceTitle = confluenceTitle;
    }

    public boolean exists() {
        return exists;
    }

    public void setExists(Boolean exists) {
        this.exists = exists;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public PageType getPageType() {
        return pageType;
    }

    public void setPageType(PageType pageType) {
        this.pageType = pageType;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getXhtml() {
        return xhtml;
    }

    public void setXhtml(String xhtml) {
        this.xhtml = xhtml;
    }

}

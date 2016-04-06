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
import org.apache.commons.lang3.StringUtils;

public class SwaggerConfluenceConfig {

    private Integer ancestorId;
    private String authentication;
    private String confluenceRestApiUrl;
    private boolean generateNumericPrefixes;
    private boolean includeTableOfContentsOnSinglePage;
    private PaginationMode paginationMode;
    private String prefix;
    private String spaceKey;
    private String swaggerSchema;
    private String title;

    public SwaggerConfluenceConfig() {
        generateNumericPrefixes = true;
        includeTableOfContentsOnSinglePage = true;
        paginationMode = PaginationMode.SINGLE_PAGE;
    }

    public Integer getAncestorId() {
        return ancestorId;
    }

    public void setAncestorId(final Integer ancestorId) {
        this.ancestorId = ancestorId;
    }

    public String getAuthentication() {
        return authentication;
    }

    public void setAuthentication(final String authentication) {
        this.authentication = authentication;
    }

    public String getConfluenceRestApiUrl() {
        return confluenceRestApiUrl;
    }

    public void setConfluenceRestApiUrl(final String confluenceRestApiUrl) {
        this.confluenceRestApiUrl = confluenceRestApiUrl;
    }

    public boolean isGenerateNumericPrefixes() {
        return generateNumericPrefixes;
    }

    public void setGenerateNumericPrefixes(final boolean generateNumericPrefixes) {
        this.generateNumericPrefixes = generateNumericPrefixes;
    }

    public boolean isIncludeTableOfContentsOnSinglePage() {
        return includeTableOfContentsOnSinglePage;
    }

    public void setIncludeTableOfContentsOnSinglePage(final boolean includeTableOfContentsOnSinglePage) {
        this.includeTableOfContentsOnSinglePage = includeTableOfContentsOnSinglePage;
    }

    public PaginationMode getPaginationMode() {
        return paginationMode;
    }

    public void setPaginationMode(final PaginationMode paginationMode) {
        this.paginationMode = paginationMode;
    }

    public void setPaginationMode(final String paginationMode) {
        switch (paginationMode) {
            case "single":
                this.paginationMode = PaginationMode.SINGLE_PAGE;
                break;
            case "category":
                this.paginationMode = PaginationMode.CATEGORY_PAGES;
                break;
            case "individual":
                this.paginationMode = PaginationMode.INDIVIDUAL_PAGES;
                break;
            default:
                throw new SwaggerConfluenceConfigurationException(
                        String.format("Invalid Pagination Mode <%s>", paginationMode)
                );
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(final String prefix) {
        if (prefix.endsWith(" ")) {
            this.prefix = prefix;
        } else if (StringUtils.isNotEmpty(prefix)){
            this.prefix = prefix + ' ';
        } else {
            this.prefix = prefix;
        }
    }

    public String getSpaceKey() {
        return spaceKey;
    }

    public void setSpaceKey(final String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public String getSwaggerSchema() {
        return swaggerSchema;
    }

    public void setSwaggerSchema(final String swaggerSchema) {
        this.swaggerSchema = swaggerSchema;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

}

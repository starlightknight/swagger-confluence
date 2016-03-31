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

public class ConfluencePageBuilder {
	private Integer ancestorId;
	private Boolean exists;
	private String id;
	private PageType pageType;
	private String title;
	private Integer version;
	private String xhtml;

	private ConfluencePageBuilder() {
	}

	public static ConfluencePageBuilder aConfluencePage() {
		return new ConfluencePageBuilder();
	}

	public ConfluencePageBuilder withAncestorId(Integer ancestorId) {
		this.ancestorId = ancestorId;
		return this;
	}

	public ConfluencePageBuilder withExists(Boolean exists) {
		this.exists = exists;
		return this;
	}

	public ConfluencePageBuilder withId(String id) {
		this.id = id;
		return this;
	}

	public ConfluencePageBuilder withPageType(PageType pageType) {
		this.pageType = pageType;
		return this;
	}

	public ConfluencePageBuilder withTitle(String title) {
		this.title = title;
		return this;
	}

	public ConfluencePageBuilder withVersion(Integer version) {
		this.version = version;
		return this;
	}

	public ConfluencePageBuilder withXhtml(String xhtml) {
		this.xhtml = xhtml;
		return this;
	}

	public ConfluencePageBuilder but() {
		return aConfluencePage().withAncestorId(ancestorId).withExists(exists).withId(id).withPageType(pageType).withTitle(title).withVersion(version).withXhtml(xhtml);
	}

	public ConfluencePage build() {
		ConfluencePage confluencePage = new ConfluencePage();
		confluencePage.setAncestorId(ancestorId);
		confluencePage.setExists(exists);
		confluencePage.setId(id);
		confluencePage.setPageType(pageType);
		confluencePage.setTitle(title);
		confluencePage.setVersion(version);
		confluencePage.setXhtml(xhtml);
		return confluencePage;
	}
}

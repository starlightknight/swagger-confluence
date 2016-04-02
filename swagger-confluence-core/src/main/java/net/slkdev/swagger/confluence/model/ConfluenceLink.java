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

public class ConfluenceLink {
	private PageType pageType;
	private String originalHref;
	private String confluenceLinkMarkup;
	private String text;

	public PageType getPageType() {
		return pageType;
	}

	public void setPageType(PageType pageType) {
		this.pageType = pageType;
	}

	public String getOriginalHref() {
		return originalHref;
	}

	public void setOriginalHref(String originalHref) {
		this.originalHref = originalHref;
	}

	public String getConfluenceLinkMarkup() {
		return confluenceLinkMarkup;
	}

	public void setConfluenceLinkMarkup(String confluenceLinkMarkup) {
		this.confluenceLinkMarkup = confluenceLinkMarkup;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}

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

public class SwaggerConfluenceConfig {

	private Integer ancestorId;
	private String authentication;
	private String confluenceRestApiUrl;
	private String prefix;
	private String spaceKey;
	private String swaggerSchema;
	private String title;

	public Integer getAncestorId() {
		return ancestorId;
	}

	public void setAncestorId(Integer ancestorId) {
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

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(final String prefix) {
		this.prefix = prefix;
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

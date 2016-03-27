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
package net.slkdev.swagger.confluence.service.impl;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import net.minidev.json.JSONObject;
import net.slkdev.swagger.confluence.config.SwaggerConfluenceConfig;
import net.slkdev.swagger.confluence.model.ConfluencePage;
import net.slkdev.swagger.confluence.service.XHtmlToConfluenceService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;

public class XHtmlToConfluenceServiceImpl implements XHtmlToConfluenceService {

	private static final Logger LOG = LoggerFactory.getLogger(XHtmlToConfluenceServiceImpl.class);

	private RestTemplate restTemplate;

	public XHtmlToConfluenceServiceImpl(final RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void postXHtmlToConfluence(final SwaggerConfluenceConfig swaggerConfluenceConfig, final String xhtml){
		LOG.info("Posting XHTML to Confluence...");

		final ConfluencePage existingPage = checkForExistingPage(swaggerConfluenceConfig);

		if(existingPage.exists()){
			LOG.info("Page {} Already Exists, Performing an Update!", existingPage.getId());
			updatePage(xhtml, existingPage, swaggerConfluenceConfig);
		}
		else {
			LOG.info("Page Does Not Exist, Creating a New Page!");
			createPage(xhtml, swaggerConfluenceConfig);
		}
	}

	private HttpHeaders buildHttpHeaders(final String confluenceAuthentication){
		final HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", String.format("Basic %s", confluenceAuthentication));
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);

		return headers;
	}

	private ConfluencePage checkForExistingPage(final SwaggerConfluenceConfig swaggerConfluenceConfig){
		final HttpHeaders httpHeaders = buildHttpHeaders(swaggerConfluenceConfig.getAuthentication());
		final HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

		final URI targetUrl = UriComponentsBuilder.fromUriString(swaggerConfluenceConfig.getConfluenceRestApiUrl())
				.path("/content")
				.queryParam("spaceKey", swaggerConfluenceConfig.getSpaceKey())
				.queryParam("title", swaggerConfluenceConfig.getPrefix()+ swaggerConfluenceConfig.getTitle())
				.queryParam("expand", "body.storage,version")
				.build()
				.toUri();

		final ResponseEntity<String> responseEntity = restTemplate.exchange(targetUrl,
				HttpMethod.GET, requestEntity, String.class);

		final String jsonBody = responseEntity.getBody();

		final ConfluencePage confluencePage = new ConfluencePage();

		try {
			final String id = JsonPath.read(jsonBody, "$.results[0].id");
			final Integer version = JsonPath.read(jsonBody, "$.results[0].version.number");
			confluencePage.setId(id);
			confluencePage.setVersion(version);
			confluencePage.setExists(true);
		}
		catch(final PathNotFoundException e){
			confluencePage.setExists(false);
		}

		return confluencePage;
	}

	private JSONObject buildPostBody(final SwaggerConfluenceConfig swaggerConfluenceConfig, final String xhtml){
		final JSONObject jsonSpaceObject = new JSONObject();
		jsonSpaceObject.put("key", swaggerConfluenceConfig.getSpaceKey());

		final JSONObject jsonStorageObject = new JSONObject();
		jsonStorageObject.put("value", xhtml);
		jsonStorageObject.put("representation","storage");

		final JSONObject jsonBodyObject = new JSONObject();
		jsonBodyObject.put("storage", jsonStorageObject);

		final JSONObject jsonObject = new JSONObject();
		jsonObject.put("type","page");
		jsonObject.put("title", swaggerConfluenceConfig.getPrefix()+ swaggerConfluenceConfig.getTitle());
		jsonObject.put("space",jsonSpaceObject);
		jsonObject.put("body",jsonBodyObject);

		return jsonObject;
	}

	private String reformatXHtml(final String inputXHtml){
		String outputXHtml = inputXHtml;
		outputXHtml = StringUtils.replace(outputXHtml, "<div class=\"sect", "<br/>\n<div class=\"sect");
		outputXHtml = StringUtils.replace(outputXHtml,
				"<h2 id=\"_overview\">Overview</h2>",
				"<h2 id=\"_overview\"><strong>Overview</strong></h2>");
		outputXHtml = StringUtils.replace(outputXHtml,
				"<h2 id=\"_definitions\">Definitions</h2>",
				"<h2 id=\"_definitions\"><strong>Definitions</strong></h2>");
		outputXHtml = StringUtils.replace(outputXHtml,
				"<h2 id=\"_paths\">Paths</h2>",
				"<h2 id=\"_paths\"><strong>Paths</strong></h2>");
		outputXHtml = StringUtils.replaceOnce(outputXHtml, "<br/>", "");
		return outputXHtml;
	}

	private void createPage(final String xhtml, final SwaggerConfluenceConfig swaggerConfluenceConfig){
		final URI targetUrl = UriComponentsBuilder.fromUriString(swaggerConfluenceConfig.getConfluenceRestApiUrl())
				.path("/content")
				.build()
				.toUri();

		final HttpHeaders httpHeaders = buildHttpHeaders(swaggerConfluenceConfig.getAuthentication());
		final String formattedXHtml = reformatXHtml(xhtml);
		final String jsonPostBody = buildPostBody(swaggerConfluenceConfig, formattedXHtml).toJSONString();
		final HttpEntity<String> requestEntity = new HttpEntity<>(jsonPostBody, httpHeaders);

		restTemplate.exchange(targetUrl, HttpMethod.POST, requestEntity, String.class);
	}

	private void updatePage(final String xhtml, final ConfluencePage page, final SwaggerConfluenceConfig swaggerConfluenceConfig){
		final URI targetUrl = UriComponentsBuilder.fromUriString(swaggerConfluenceConfig.getConfluenceRestApiUrl())
				.path(String.format("/content/%s", page.getId()))
				.build()
				.toUri();

		final HttpHeaders httpHeaders = buildHttpHeaders(swaggerConfluenceConfig.getAuthentication());

		final JSONObject postVersionObject = new JSONObject();
		postVersionObject.put("number", page.getVersion()+1);

		final String formattedXHtml = reformatXHtml(xhtml);
		final JSONObject postBody = buildPostBody(swaggerConfluenceConfig, formattedXHtml);
		postBody.put("id", page.getId());
		postBody.put("version", postVersionObject);

		final HttpEntity<String> requestEntity = new HttpEntity<>(postBody.toJSONString(), httpHeaders);

		restTemplate.exchange(targetUrl, HttpMethod.PUT, requestEntity, String.class);
	}

}

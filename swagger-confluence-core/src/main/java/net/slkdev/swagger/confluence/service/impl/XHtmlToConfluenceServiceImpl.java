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
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import net.slkdev.swagger.confluence.config.SwaggerConfluenceConfig;
import net.slkdev.swagger.confluence.constants.PageType;
import net.slkdev.swagger.confluence.constants.PaginationMode;
import net.slkdev.swagger.confluence.model.ConfluencePage;
import net.slkdev.swagger.confluence.model.ConfluencePageBuilder;
import net.slkdev.swagger.confluence.service.XHtmlToConfluenceService;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.minidev.json.parser.JSONParser.DEFAULT_PERMISSIVE_MODE;

public class XHtmlToConfluenceServiceImpl implements XHtmlToConfluenceService {

	private static final Logger LOG = LoggerFactory.getLogger(XHtmlToConfluenceServiceImpl.class);
	private static final ThreadLocal<SwaggerConfluenceConfig> SWAGGER_CONFLUENCE_CONFIG = new ThreadLocal<>();

	private RestTemplate restTemplate;

	public XHtmlToConfluenceServiceImpl(final RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void postXHtmlToConfluence(final SwaggerConfluenceConfig swaggerConfluenceConfig, final String xhtml) {
		LOG.info("Posting XHTML to Confluence...");
		SWAGGER_CONFLUENCE_CONFIG.set(swaggerConfluenceConfig);

		final List<ConfluencePage> confluencePages = handlePagination(xhtml);

		Integer categoryAncestorId = null;
		Integer individualAncestorId = null;

		for (final ConfluencePage confluencePage : confluencePages) {
			final PageType pageType = confluencePage.getPageType();

			LOG.info("PROCESSING PAGE: {} --> {}", confluencePage.getPageType(), confluencePage.getXhtml());

			switch (pageType){
				case ROOT:
					confluencePage.setAncestorId(swaggerConfluenceConfig.getAncestorId());
					break;
				case CATEGORY:
					confluencePage.setAncestorId(categoryAncestorId);
					break;
				default:
					confluencePage.setAncestorId(individualAncestorId);
			}

			addExistingPageData(confluencePage);

			if (confluencePage.exists()) {
				LOG.info("Page {} Already Exists, Performing an Update!", confluencePage.getId());
				updatePage(confluencePage);
			}
			else {
				LOG.info("Page Does Not Exist, Creating a New Page!");
				createPage(confluencePage);
			}

			if(pageType == PageType.ROOT){
				categoryAncestorId = confluencePage.getAncestorId();
			}
			else if(pageType == PageType.CATEGORY){
				individualAncestorId = confluencePage.getAncestorId();
			}
		}
	}

	private HttpHeaders buildHttpHeaders(final String confluenceAuthentication) {
		final HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", String.format("Basic %s", confluenceAuthentication));
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);

		return headers;
	}

	private List<ConfluencePage> handlePagination(final String xhtml) {
		final List<ConfluencePage> confluencePages = new ArrayList<>();
		final SwaggerConfluenceConfig swaggerConfluenceConfig = SWAGGER_CONFLUENCE_CONFIG.get();

		final PaginationMode paginationMode = swaggerConfluenceConfig.getPaginationMode();
		final String prefix = swaggerConfluenceConfig.getPrefix();

		// Parse the full document so we can start transforming it.
		final Document originalDocument = Jsoup.parse(xhtml, "utf-8", Parser.xmlParser());
		originalDocument.outputSettings().prettyPrint(false);
		originalDocument.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);
		originalDocument.outputSettings().charset("UTF-8");

		final Document transformedDocument = originalDocument.clone();
		final Elements categoryElements = transformedDocument.select(".sect1");

		// Remove ToC form the transformed document
		final Elements toc = transformedDocument.select(".toc");
		toc.html("");
		toc.unwrap();

		// For Single Page Mode, the incoming XHTML can be used directly.
		if (paginationMode == PaginationMode.SINGLE_PAGE) {
			final ConfluencePage confluencePage = ConfluencePageBuilder.aConfluencePage()
					.withPageType(PageType.ROOT)
					.withTitle(prefix + swaggerConfluenceConfig.getTitle())
					.withXhtml(transformedDocument.html()).build();
			confluencePages.add(confluencePage);

			return confluencePages;
		}

		// Before beginning further processing, we need to know if we're in individual
		// page mode or not, as that will effect how we split the DOM. If we're in this
		// mode then the category pages will contain inner table of contents.
		final boolean individualPages = (paginationMode == PaginationMode.INDIVIDUAL_PAGES);

		// From here on, if we're still proceeding then we know the meat of the document
		// will go in sub-pages. So for the master page, we will use the table of contents
		final Elements tocElements = originalDocument.select(".toc");

		final List<String> innerTocXHtmlList = new ArrayList<>();
		final Elements innerTocElements = originalDocument.select(".sectlevel2");

		for (final Element innerTocElement : innerTocElements) {
			// If we're in individual page mode, then we collect the inner ToCs
			if (individualPages) {
				innerTocXHtmlList.add(innerTocElement.html());
			}
			// If we're in category page mode, then we strip out the inner table of contents.
			else {
				innerTocElement.html("");
				innerTocElement.unwrap();
			}
		}

		// Build the Root Page w/ the Appropriate Level of Table of Contents
		final ConfluencePage rootConfluencePage = ConfluencePageBuilder.aConfluencePage()
				.withPageType(PageType.ROOT)
				.withTitle(prefix + swaggerConfluenceConfig.getTitle())
				.withXhtml(tocElements.html()).build();
		confluencePages.add(rootConfluencePage);

		int category = 0;

		// Now we process the category pages
		for (final Element categoryElement : categoryElements) {
			// Fetch the title from the first child, which is the header element
			final String categoryTitle = categoryElement.children().first().text();

			// If we're in individual mode then we need these to be sub table of contents
			if (individualPages) {
				final ConfluencePage categoryConfluencePage = ConfluencePageBuilder.aConfluencePage()
						.withPageType(PageType.CATEGORY)
						.withTitle(prefix + categoryTitle)
						.withXhtml(innerTocXHtmlList.get(category)).build();
				confluencePages.add(categoryConfluencePage);

				final Elements individualElements = categoryElement.getElementsByClass("sect2");

				for(final Element individualElement : individualElements){
					final String individualTitle = individualElement.children().first().text();
					final ConfluencePage individualConfluencePage = ConfluencePageBuilder.aConfluencePage()
							.withPageType(PageType.INDIVIDUAL)
							.withTitle(prefix + individualTitle)
							.withXhtml(individualElement.html()).build();
					confluencePages.add(individualConfluencePage);
				}

				category++;
				continue;
			}

			// If we're in category mode, we use the remaining page data
			final ConfluencePage categoryConfluencePage = ConfluencePageBuilder.aConfluencePage()
					.withPageType(PageType.CATEGORY)
					.withTitle(prefix + categoryTitle)
					.withXhtml(categoryElement.html()).build();
			confluencePages.add(categoryConfluencePage);
		}

		return confluencePages;
	}

	private void addExistingPageData(final ConfluencePage confluencePage) {
		final SwaggerConfluenceConfig swaggerConfluenceConfig = SWAGGER_CONFLUENCE_CONFIG.get();

		final HttpHeaders httpHeaders = buildHttpHeaders(swaggerConfluenceConfig.getAuthentication());
		final HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

		final URI targetUrl = UriComponentsBuilder.fromUriString(swaggerConfluenceConfig.getConfluenceRestApiUrl())
				.path("/content")
				.queryParam("spaceKey", swaggerConfluenceConfig.getSpaceKey())
				.queryParam("title", confluencePage.getTitle())
				.queryParam("expand", "body.storage,version")
				.build()
				.toUri();

		final ResponseEntity<String> responseEntity = restTemplate.exchange(targetUrl,
				HttpMethod.GET, requestEntity, String.class);

		final String jsonBody = responseEntity.getBody();

		try {
			LOG.info("GET RESPONSE: "+jsonBody);
			final String id = JsonPath.read(jsonBody, "$.results[0].id");
			final Integer version = JsonPath.read(jsonBody, "$.results[0].version.number");
			confluencePage.setId(id);
			confluencePage.setVersion(version);
			confluencePage.setExists(true);
		} catch (final PathNotFoundException e) {
			confluencePage.setExists(false);
		}
	}

	private JSONObject buildPostBody(final Integer ancestorId, final String pageTitle, final String xhtml) {
		final SwaggerConfluenceConfig swaggerConfluenceConfig = SWAGGER_CONFLUENCE_CONFIG.get();

		final JSONObject jsonSpaceObject = new JSONObject();
		jsonSpaceObject.put("key", swaggerConfluenceConfig.getSpaceKey());

		final JSONObject jsonStorageObject = new JSONObject();
		jsonStorageObject.put("value", xhtml);
		jsonStorageObject.put("representation", "storage");

		final JSONObject jsonBodyObject = new JSONObject();
		jsonBodyObject.put("storage", jsonStorageObject);

		final JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", "page");
		jsonObject.put("title", pageTitle);
		jsonObject.put("space", jsonSpaceObject);
		jsonObject.put("body", jsonBodyObject);

		if (ancestorId != null) {
			final JSONObject ancestor = new JSONObject();
			ancestor.put("type", "page");
			ancestor.put("id", ancestorId);

			final JSONArray ancestors = new JSONArray();
			ancestors.add(ancestor);

			jsonObject.put("ancestors", ancestors);
		}

		return jsonObject;
	}

	private String reformatXHtml(final String inputXHtml) {
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

	private Integer getPageIdFromResponse(final HttpEntity<String> responseEntity){
		final String responseJson = responseEntity.getBody();
		final JSONParser jsonParser = new JSONParser(DEFAULT_PERMISSIVE_MODE);

		try {
			final JSONObject response = jsonParser.parse(responseJson, JSONObject.class);
			return Integer.valueOf((String)response.get("id"));
		}
		catch (ParseException e) {
			throw new RuntimeException("Error Parsing JSON Resposne from Confluence!", e);
		}
	}

	private void createPage(final ConfluencePage page) {
		final SwaggerConfluenceConfig swaggerConfluenceConfig = SWAGGER_CONFLUENCE_CONFIG.get();
		final URI targetUrl = UriComponentsBuilder.fromUriString(swaggerConfluenceConfig.getConfluenceRestApiUrl())
				.path("/content")
				.build()
				.toUri();

		final HttpHeaders httpHeaders = buildHttpHeaders(swaggerConfluenceConfig.getAuthentication());
		final String formattedXHtml = reformatXHtml(page.getXhtml());
		final String jsonPostBody = buildPostBody(page.getAncestorId(), page.getTitle(), formattedXHtml).toJSONString();

		LOG.info("REQUEST: "+jsonPostBody);

		final HttpEntity<String> requestEntity = new HttpEntity<>(jsonPostBody, httpHeaders);

		final HttpEntity<String> responseEntity = restTemplate.exchange(targetUrl,
				HttpMethod.POST, requestEntity, String.class);

		LOG.info("RESPONSE: "+responseEntity.getBody());

		final Integer pageId = getPageIdFromResponse(responseEntity);
		page.setAncestorId(pageId);
	}

	private void updatePage(final ConfluencePage page) {
		final SwaggerConfluenceConfig swaggerConfluenceConfig = SWAGGER_CONFLUENCE_CONFIG.get();

		final URI targetUrl = UriComponentsBuilder.fromUriString(swaggerConfluenceConfig.getConfluenceRestApiUrl())
				.path(String.format("/content/%s", page.getId()))
				.build()
				.toUri();

		final HttpHeaders httpHeaders = buildHttpHeaders(swaggerConfluenceConfig.getAuthentication());

		final JSONObject postVersionObject = new JSONObject();
		postVersionObject.put("number", page.getVersion() + 1);

		final String formattedXHtml = reformatXHtml(page.getXhtml());
		final JSONObject postBody = buildPostBody(page.getAncestorId(), page.getTitle(), formattedXHtml);
		postBody.put("id", page.getId());
		postBody.put("version", postVersionObject);

		final HttpEntity<String> requestEntity = new HttpEntity<>(postBody.toJSONString(), httpHeaders);

		restTemplate.exchange(targetUrl, HttpMethod.PUT, requestEntity, String.class);
	}

}

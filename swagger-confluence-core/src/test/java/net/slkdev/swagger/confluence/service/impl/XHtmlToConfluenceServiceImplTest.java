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

import net.slkdev.swagger.confluence.config.SwaggerConfluenceConfig;
import net.slkdev.swagger.confluence.constants.PaginationMode;
import net.slkdev.swagger.confluence.service.XHtmlToConfluenceService;
import org.apache.commons.codec.binary.Base64;
import org.asciidoctor.internal.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class XHtmlToConfluenceServiceImplTest {

	private static final String GET_RESPONSE_FOUND = "{\"results\":[{\"id\":\"1277959\",\"type\":\"" +
			"page\",\"title\":\"Test\",\"version\":{\"number\":1},\"body\":{\"storage\":{\"value\":" +
			"\"\",\"representation\":\"storage\"}},\"ancestors\":[{\"id\":\"1474805\"}]}]}";

	private static final String GET_RESPONSE_NOT_FOUND = "{\"results\":[]}";

	private static final String POST_RESPONSE = "{\"id\":\"1\"}";

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private ResponseEntity<String> responseEntity;

	private XHtmlToConfluenceService xHtmlToConfluenceService;

	@Before
	public void setUp(){
		xHtmlToConfluenceService = new XHtmlToConfluenceServiceImpl(restTemplate);
	}

	@Test
	public void testCreatePageWithPaginationModeSingleWithOrphanFailSafe(){
		final SwaggerConfluenceConfig swaggerConfluenceConfig = getTestSwaggerConfluenceConfig();
        swaggerConfluenceConfig.setAncestorId(null);

		final String xhtml = IOUtils.readFull(
				AsciiDocToXHtmlServiceImplTest.class.getResourceAsStream(
						"/swagger-petstore-xhtml-example.html")
		);

        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET),
                any(RequestEntity.class), eq(String.class))).thenReturn(responseEntity,
                responseEntity);
		when(restTemplate.exchange(any(URI.class), eq(HttpMethod.POST),
				any(HttpEntity.class), eq(String.class))).thenReturn(responseEntity);
		when(responseEntity.getBody()).thenReturn(GET_RESPONSE_NOT_FOUND, GET_RESPONSE_FOUND,
                POST_RESPONSE);

		final ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

		xHtmlToConfluenceService.postXHtmlToConfluence(swaggerConfluenceConfig, xhtml);

		verify(restTemplate, times(2)).exchange(any(URI.class), eq(HttpMethod.GET),
				any(RequestEntity.class), eq(String.class));
		verify(restTemplate).exchange(any(URI.class), eq(HttpMethod.POST),
				httpEntityCaptor.capture(), eq(String.class));

		final HttpEntity<String> capturedHttpEntity = httpEntityCaptor.getValue();

		final String expectedPostBody = IOUtils.readFull(
				AsciiDocToXHtmlServiceImplTest.class.getResourceAsStream(
						"/swagger-confluence-create-json-body-example.json")
		);

		assertNotNull("Failed to Capture RequestEntity for POST", capturedHttpEntity);
		assertEquals("Unexpected JSON Post Body", expectedPostBody, capturedHttpEntity.getBody());
	}

	@Test
	public void testUpdatePageWithPaginationModeSingleAndNoTableOfContents(){
		final SwaggerConfluenceConfig swaggerConfluenceConfig = getTestSwaggerConfluenceConfig();
        swaggerConfluenceConfig.setIncludeTableOfContentsOnSinglePage(false);

		final String xhtml = IOUtils.readFull(
				AsciiDocToXHtmlServiceImplTest.class.getResourceAsStream(
						"/swagger-petstore-xhtml-example.html")
		);

		final ResponseEntity<String> postResponseEntity = new ResponseEntity<>(POST_RESPONSE, HttpStatus.OK);

		when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET),
				any(RequestEntity.class), eq(String.class))).thenReturn(responseEntity);
		when(responseEntity.getBody()).thenReturn(GET_RESPONSE_FOUND);
		when(restTemplate.exchange(any(URI.class), eq(HttpMethod.PUT),
				any(RequestEntity.class), eq(String.class))).thenReturn(postResponseEntity);

        final ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

		xHtmlToConfluenceService.postXHtmlToConfluence(swaggerConfluenceConfig, xhtml);

        verify(restTemplate).exchange(any(URI.class), eq(HttpMethod.GET),
				any(RequestEntity.class), eq(String.class));
		verify(restTemplate).exchange(any(URI.class), eq(HttpMethod.PUT),
                httpEntityCaptor.capture(), eq(String.class));

        final HttpEntity<String> capturedHttpEntity = httpEntityCaptor.getValue();

        final String expectedPostBody = IOUtils.readFull(
                AsciiDocToXHtmlServiceImplTest.class.getResourceAsStream(
                        "/swagger-confluence-update-json-body-example.json")
        );

        assertNotNull("Failed to Capture RequestEntity for POST", capturedHttpEntity);
        assertEquals("Unexpected JSON Post Body", expectedPostBody, capturedHttpEntity.getBody());
	}

	@Test
	public void testCreatePageWithPaginationModeCategory(){
		final SwaggerConfluenceConfig swaggerConfluenceConfig = getTestSwaggerConfluenceConfig();
		swaggerConfluenceConfig.setPaginationMode(PaginationMode.CATEGORY_PAGES);

		final String xhtml = IOUtils.readFull(
				AsciiDocToXHtmlServiceImplTest.class.getResourceAsStream(
						"/swagger-petstore-xhtml-example.html")
		);

		for(int i = 0; i < 4; i++) {
			when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET),
					any(RequestEntity.class), eq(String.class))).thenReturn(responseEntity);
			when(responseEntity.getBody()).thenReturn(GET_RESPONSE_NOT_FOUND);
			when(restTemplate.exchange(any(URI.class), eq(HttpMethod.POST),
					any(HttpEntity.class), eq(String.class))).thenReturn(responseEntity);
			when(responseEntity.getBody()).thenReturn(POST_RESPONSE);
		}

		final ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

		xHtmlToConfluenceService.postXHtmlToConfluence(swaggerConfluenceConfig, xhtml);

		verify(restTemplate, times(4)).exchange(any(URI.class), eq(HttpMethod.GET),
				any(RequestEntity.class), eq(String.class));
		verify(restTemplate, times(4)).exchange(any(URI.class), eq(HttpMethod.POST),
				httpEntityCaptor.capture(), eq(String.class));

		final HttpEntity<String> capturedHttpEntity = httpEntityCaptor.getValue();

		final String expectedPostBody = IOUtils.readFull(
				AsciiDocToXHtmlServiceImplTest.class.getResourceAsStream(
						"/swagger-confluence-create-json-body-definitions-example.json")
		);

		assertNotNull("Failed to Capture RequeestEntity for POST", capturedHttpEntity);
		// We'll do a full check on the last page versus a resource; not doing all of them as it
		// would be a pain to maintain, but this should give us a nod of confidence.
		assertEquals("Unexpected JSON Post Body", expectedPostBody, capturedHttpEntity.getBody());
	}

	@Test
	public void testUpdatePageWithPaginationModeCategory(){
		final SwaggerConfluenceConfig swaggerConfluenceConfig = getTestSwaggerConfluenceConfig();
		swaggerConfluenceConfig.setPaginationMode(PaginationMode.CATEGORY_PAGES);

		final String xhtml = IOUtils.readFull(
				AsciiDocToXHtmlServiceImplTest.class.getResourceAsStream(
						"/swagger-petstore-xhtml-example.html")
		);

		final ResponseEntity<String> postResponseEntity = new ResponseEntity<>(POST_RESPONSE, HttpStatus.OK);

		for(int i = 0; i < 4; i++) {
			when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET),
					any(RequestEntity.class), eq(String.class))).thenReturn(responseEntity);
			when(responseEntity.getBody()).thenReturn(GET_RESPONSE_FOUND);
			when(restTemplate.exchange(any(URI.class), eq(HttpMethod.PUT),
					any(RequestEntity.class), eq(String.class))).thenReturn(postResponseEntity);
		}

        final ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

		xHtmlToConfluenceService.postXHtmlToConfluence(swaggerConfluenceConfig, xhtml);

		verify(restTemplate, times(4)).exchange(any(URI.class), eq(HttpMethod.GET),
				any(RequestEntity.class), eq(String.class));
        verify(restTemplate, times(4)).exchange(any(URI.class), eq(HttpMethod.PUT),
                httpEntityCaptor.capture(), eq(String.class));

        final HttpEntity<String> capturedHttpEntity = httpEntityCaptor.getValue();

        final String expectedPostBody = IOUtils.readFull(
                AsciiDocToXHtmlServiceImplTest.class.getResourceAsStream(
                        "/swagger-confluence-update-json-body-definitions-example.json")
        );

        assertNotNull("Failed to Capture RequeestEntity for POST", capturedHttpEntity);
        // We'll do a full check on the last page versus a resource; not doing all of them as it
        // would be a pain to maintain, but this should give us a nod of confidence.
        assertEquals("Unexpected JSON Post Body", expectedPostBody, capturedHttpEntity.getBody());
	}

    @Test
    public void testCreatePageWithPaginationModeIndividual(){
        final SwaggerConfluenceConfig swaggerConfluenceConfig = getTestSwaggerConfluenceConfig();
        swaggerConfluenceConfig.setPaginationMode(PaginationMode.INDIVIDUAL_PAGES);

        final String xhtml = IOUtils.readFull(
                AsciiDocToXHtmlServiceImplTest.class.getResourceAsStream(
                        "/swagger-petstore-xhtml-example.html")
        );

        for(int i = 0; i < 31; i++) {
            when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET),
                    any(RequestEntity.class), eq(String.class))).thenReturn(responseEntity);
            when(responseEntity.getBody()).thenReturn(GET_RESPONSE_NOT_FOUND);
            when(restTemplate.exchange(any(URI.class), eq(HttpMethod.POST),
                    any(HttpEntity.class), eq(String.class))).thenReturn(responseEntity);
            when(responseEntity.getBody()).thenReturn(POST_RESPONSE);
        }

        final ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        xHtmlToConfluenceService.postXHtmlToConfluence(swaggerConfluenceConfig, xhtml);

        verify(restTemplate, times(31)).exchange(any(URI.class), eq(HttpMethod.GET),
                any(RequestEntity.class), eq(String.class));
        verify(restTemplate, times(31)).exchange(any(URI.class), eq(HttpMethod.POST),
                httpEntityCaptor.capture(), eq(String.class));

        final HttpEntity<String> capturedHttpEntity = httpEntityCaptor.getValue();

        final String expectedPostBody = IOUtils.readFull(
                AsciiDocToXHtmlServiceImplTest.class.getResourceAsStream(
                        "/swagger-confluence-create-json-body-user-example.json")
        );

        assertNotNull("Failed to Capture RequestEntity for POST", capturedHttpEntity);
        // We'll do a full check on the last page versus a resource; not doing all of them as it
        // would be a pain to maintain, but this should give us a nod of confidence.
        assertEquals("Unexpected JSON Post Body", expectedPostBody, capturedHttpEntity.getBody());
    }

    @Test
    public void testUpdatePageWithPaginationModeIndividual(){
        final SwaggerConfluenceConfig swaggerConfluenceConfig = getTestSwaggerConfluenceConfig();
        swaggerConfluenceConfig.setPaginationMode(PaginationMode.INDIVIDUAL_PAGES);

        final String xhtml = IOUtils.readFull(
                AsciiDocToXHtmlServiceImplTest.class.getResourceAsStream(
                        "/swagger-petstore-xhtml-example.html")
        );

        final ResponseEntity<String> postResponseEntity = new ResponseEntity<>(POST_RESPONSE, HttpStatus.OK);

        for(int i = 0; i < 31; i++) {
            when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET),
                    any(RequestEntity.class), eq(String.class))).thenReturn(responseEntity);
            when(responseEntity.getBody()).thenReturn(GET_RESPONSE_FOUND);
            when(restTemplate.exchange(any(URI.class), eq(HttpMethod.PUT),
                    any(RequestEntity.class), eq(String.class))).thenReturn(postResponseEntity);
        }

        final ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        xHtmlToConfluenceService.postXHtmlToConfluence(swaggerConfluenceConfig, xhtml);

        verify(restTemplate, times(31)).exchange(any(URI.class), eq(HttpMethod.GET),
                any(RequestEntity.class), eq(String.class));
        verify(restTemplate, times(31)).exchange(any(URI.class), eq(HttpMethod.PUT),
                httpEntityCaptor.capture(), eq(String.class));

        final HttpEntity<String> capturedHttpEntity = httpEntityCaptor.getValue();

        final String expectedPostBody = IOUtils.readFull(
                AsciiDocToXHtmlServiceImplTest.class.getResourceAsStream(
                        "/swagger-confluence-update-json-body-user-example.json")
        );

        assertNotNull("Failed to Capture RequestEntity for POST", capturedHttpEntity);
        // We'll do a full check on the last page versus a resource; not doing all of them as it
        // would be a pain to maintain, but this should give us a nod of confidence.
        assertEquals("Unexpected JSON Post Body", expectedPostBody, capturedHttpEntity.getBody());
    }

    private SwaggerConfluenceConfig getTestSwaggerConfluenceConfig(){
		final SwaggerConfluenceConfig swaggerConfluenceConfig = new SwaggerConfluenceConfig();

		swaggerConfluenceConfig.setAuthentication(getTestAuthentication());
		swaggerConfluenceConfig.setAncestorId(0);
		swaggerConfluenceConfig.setConfluenceRestApiUrl("https://localhost/confluence/rest/api/");
		swaggerConfluenceConfig.setPrefix("");
		swaggerConfluenceConfig.setSpaceKey("DOC");
		swaggerConfluenceConfig.setTitle("Test");

		return swaggerConfluenceConfig;
	}

	private String getTestAuthentication(){
		final String plainCreds = "test:password";
		final byte[] plainCredsBytes = plainCreds.getBytes();
		final byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		return new String(base64CredsBytes);
	}

}

/*
 * Copyright 2023-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.ai.openai.image;

import java.util.List;
import java.util.Map;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.Test;

import org.springframework.ai.content.Media;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.ai.openai.metadata.OpenAiImageGenerationMetadata;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.core.io.ByteArrayResource;

import static org.assertj.core.api.Assertions.assertThat;

class OpenAiImageModelHttpTests {

	@Test
	void editRequestUsesCustomMultipartFieldNameAndDoesNotLeakControlHeader() throws Exception {
		try (MockWebServer server = new MockWebServer()) {
			server.enqueue(new MockResponse().setResponseCode(200)
				.setHeader("Content-Type", "application/json")
				.setBody("{\"created\":123,\"data\":[{\"b64_json\":\"aW1hZ2U=\"}]}"));
			server.start();

			ByteArrayResource mask = new ByteArrayResource(new byte[] { 4, 5, 6 }) {
				@Override
				public String getFilename() {
					return "mask";
				}
			};
			OpenAiImageOptions options = OpenAiImageOptions.builder()
				.baseUrl(server.url("/v1").toString())
				.apiKey("test-key")
				.model("gpt-image-1")
				.imageFieldName("source_image")
				.mask(mask)
				.httpHeaders(Map.of("x-request-id", "request-123"))
				.build();
			OpenAiImageModel model = new OpenAiImageModel(options);
			Media sourceImage = Media.builder()
				.mimeType(Media.Format.IMAGE_PNG)
				.name("source")
				.data(new byte[] { 1, 2, 3 })
				.build();

			var response = model.call(new ImagePrompt(List.of(sourceImage), "add flowers", options));

			assertThat(response.getResult().getOutput().getB64Json()).isEqualTo("aW1hZ2U=");
			RecordedRequest request = server.takeRequest();
			String body = request.getBody().readUtf8();
			assertThat(request.getPath()).isEqualTo("/v1/images/edits");
			assertThat(request.getHeader("x-request-id")).isEqualTo("request-123");
			assertThat(request.getHeader("X-Spring-Ai-Image-Field-Name")).isNull();
			assertThat(body).contains("name=\"source_image\"; filename=\"source.png\"")
				.contains("name=\"mask\"; filename=\"mask.png\"")
				.doesNotContain("name=\"image[]\"");
		}
	}

	@Test
	void legacyRestEditRequestSupportsMaskAndCustomRequestOptions() throws Exception {
		try (MockWebServer server = new MockWebServer()) {
			server.enqueue(new MockResponse().setResponseCode(200)
				.setHeader("Content-Type", "application/json")
				.setBody("{\"created\":123,\"data\":[{\"b64_json\":\"aW1hZ2U=\",\"revised_prompt\":\"flowers\"}]}"));
			server.start();

			OpenAiImageApi imageApi = OpenAiImageApi.builder()
				.baseUrl(server.url("/").toString())
				.apiKey("test-key")
				.imageEditPath("custom/images/edits")
				.build();
			ByteArrayResource mask = new ByteArrayResource(new byte[] { 4, 5, 6 }) {
				@Override
				public String getFilename() {
					return "mask";
				}
			};
			OpenAiImageOptions options = OpenAiImageOptions.builder()
				.model("gpt-image-1")
				.imageFieldName("source_image")
				.mask(mask)
				.httpHeaders(Map.of("x-request-id", "request-123"))
				.build();
			OpenAiImageModel model = new OpenAiImageModel(imageApi, options, RetryUtils.SHORT_RETRY_TEMPLATE);
			Media sourceImage = Media.builder()
				.mimeType(Media.Format.IMAGE_PNG)
				.name("source")
				.data(new byte[] { 1, 2, 3 })
				.build();

			var response = model.call(new ImagePrompt(List.of(sourceImage), "add flowers"));

			assertThat(response.getResult().getOutput().getB64Json()).isEqualTo("aW1hZ2U=");
			assertThat(response.getResult().getMetadata()).isEqualTo(new OpenAiImageGenerationMetadata("flowers"));
			assertThat(response.getMetadata().getCreated()).isEqualTo(123L);
			RecordedRequest request = server.takeRequest();
			String body = request.getBody().readUtf8();
			assertThat(request.getPath()).isEqualTo("/custom/images/edits");
			assertThat(request.getHeader("Authorization")).isEqualTo("Bearer test-key");
			assertThat(request.getHeader("x-request-id")).isEqualTo("request-123");
			assertThat(body).contains("name=\"source_image\"; filename=\"source.png\"")
				.contains("name=\"mask\"; filename=\"mask.png\"")
				.contains("name=\"prompt\"")
				.contains("add flowers")
				.doesNotContain("name=\"image[]\"");
		}
	}

	@Test
	void legacyRestApiAcceptsNullAdditionalHeaders() throws Exception {
		try (MockWebServer server = new MockWebServer()) {
			server.enqueue(new MockResponse().setResponseCode(200)
				.setHeader("Content-Type", "application/json")
				.setBody("{\"created\":123,\"data\":[{\"url\":\"https://example.com/image.png\"}]}"));
			server.start();

			OpenAiImageApi imageApi = OpenAiImageApi.builder()
				.baseUrl(server.url("/").toString())
				.apiKey("test-key")
				.build();

			var response = imageApi.createImage(new OpenAiImageApi.OpenAiImageRequest("draw a flower", "gpt-image-1"),
					null);

			assertThat(response.getBody()).isNotNull();
			assertThat(response.getBody().data()).singleElement()
				.satisfies(image -> assertThat(image.url()).isEqualTo("https://example.com/image.png"));
			RecordedRequest request = server.takeRequest();
			assertThat(request.getPath()).isEqualTo("/v1/images/generations");
			assertThat(request.getHeader("Authorization")).isEqualTo("Bearer test-key");
		}
	}

}

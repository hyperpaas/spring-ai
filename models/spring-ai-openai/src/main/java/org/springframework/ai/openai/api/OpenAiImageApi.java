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

package org.springframework.ai.openai.api;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import org.springframework.ai.content.Media;
import org.springframework.ai.model.ApiKey;
import org.springframework.ai.model.NoopApiKey;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.openai.api.common.OpenAiApiConstants;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MimeType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;

/**
 * Compatibility REST client for the OpenAI image generation and image editing APIs.
 *
 * <p>
 * This API is retained for applications that require custom endpoint paths or a
 * {@link RestClient}. New applications can use the OpenAI SDK-backed
 * {@code OpenAiImageModel} constructors instead.
 *
 * @author lambochen
 * @author Filip Hrisafov
 * @since 0.8.0
 */
public class OpenAiImageApi {

	public static final String DEFAULT_IMAGE_MODEL = ImageModel.DALL_E_3.getValue();

	public static final String DEFAULT_IMAGE_EDIT_PATH = "v1/images/edits";

	private final RestClient restClient;

	private final String imagesPath;

	private final String imageEditPath;

	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Create a new OpenAI image API.
	 * @param baseUrl the base URL for the OpenAI API
	 * @param apiKey the OpenAI API key
	 * @param headers the connection-level HTTP headers
	 * @param imagesPath the image generation path
	 * @param imageEditPath the image editing path
	 * @param restClientBuilder the REST client builder
	 * @param responseErrorHandler the response error handler
	 */
	public OpenAiImageApi(String baseUrl, ApiKey apiKey, MultiValueMap<String, String> headers, String imagesPath,
			String imageEditPath, RestClient.Builder restClientBuilder, ResponseErrorHandler responseErrorHandler) {
		Assert.hasText(baseUrl, "baseUrl cannot be null or empty");
		Assert.notNull(apiKey, "apiKey cannot be null");
		Assert.notNull(headers, "headers cannot be null");
		Assert.hasText(imagesPath, "imagesPath cannot be null or empty");
		Assert.hasText(imageEditPath, "imageEditPath cannot be null or empty");
		Assert.notNull(restClientBuilder, "restClientBuilder cannot be null");
		Assert.notNull(responseErrorHandler, "responseErrorHandler cannot be null");

		this.restClient = restClientBuilder.clone().baseUrl(baseUrl).defaultHeaders(httpHeaders -> {
			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
			headers.forEach(httpHeaders::addAll);
		}).defaultStatusHandler(responseErrorHandler).defaultRequest(request -> {
			if (!(apiKey instanceof NoopApiKey)) {
				request.header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey.getValue());
			}
		}).build();
		this.imagesPath = imagesPath;
		this.imageEditPath = imageEditPath;
	}

	public ResponseEntity<OpenAiImageResponse> createImage(OpenAiImageRequest request,
			@Nullable MultiValueMap<String, String> additionalHttpHeaders) {
		Assert.notNull(request, "Image request cannot be null");
		Assert.hasLength(request.prompt(), "Prompt cannot be empty");

		return this.restClient.post()
			.uri(this.imagesPath)
			.headers(headers -> addHeaders(headers, additionalHttpHeaders))
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.retrieve()
			.toEntity(OpenAiImageResponse.class);
	}

	public ResponseEntity<OpenAiImageResponse> createImageEdit(OpenAiImageEditRequest request,
			@Nullable MultiValueMap<String, String> additionalHttpHeaders) {
		Assert.notNull(request, "Image request cannot be null");
		Assert.hasLength(request.prompt(), "Prompt cannot be empty");
		Assert.notEmpty(request.image(), "Image cannot be empty");

		MultiValueMap<String, Object> multipartBody = new LinkedMultiValueMap<>();
		String imageFieldName = Objects.requireNonNull(request.imageFieldName(), "Image field name cannot be null");
		for (Media image : request.image()) {
			multipartBody.add(imageFieldName, asResource(image));
		}
		multipartBody.add("prompt", request.prompt());
		addIfPresent(multipartBody, "model", request.model());
		addIfPresent(multipartBody, "response_format", request.responseFormat());
		addIfPresent(multipartBody, "n", request.n());
		addIfPresent(multipartBody, "quality", request.quality());
		addIfPresent(multipartBody, "size", request.size());
		addIfPresent(multipartBody, "user", request.user());
		if (request.mask() != null) {
			multipartBody.add("mask", asResource(request.mask()));
		}

		ResponseEntity<String> rawResponse = this.restClient.post()
			.uri(this.imageEditPath)
			.headers(headers -> addHeaders(headers, additionalHttpHeaders))
			.contentType(MediaType.MULTIPART_FORM_DATA)
			.body(multipartBody)
			.retrieve()
			.toEntity(String.class);

		try {
			String responseBody = Objects.requireNonNull(rawResponse.getBody(),
					"OpenAI image editing response body must not be null");
			OpenAiImageResponse response = this.objectMapper.readValue(responseBody, OpenAiImageResponse.class);
			return ResponseEntity.status(rawResponse.getStatusCode()).headers(rawResponse.getHeaders()).body(response);
		}
		catch (JacksonException ex) {
			throw new IllegalStateException("Failed to parse OpenAI image editing response", ex);
		}
	}

	private static Resource asResource(Media media) {
		String filename = media.getName();
		Assert.hasText(filename, "Media filename cannot be null or empty");
		if (!filename.contains(".")) {
			MimeType mimeType = media.getMimeType();
			Assert.notNull(mimeType, "Media without a filename extension must have a MIME type");
			filename = filename + "." + mimeType.getSubtype();
		}
		String resolvedFilename = filename;
		return new ByteArrayResource(media.getDataAsByteArray()) {
			@Override
			public String getFilename() {
				return resolvedFilename;
			}
		};
	}

	private static void addIfPresent(MultiValueMap<String, Object> body, String name, @Nullable Object value) {
		if (value != null) {
			body.add(name, value);
		}
	}

	private static void addHeaders(HttpHeaders headers, @Nullable MultiValueMap<String, String> additionalHttpHeaders) {
		if (additionalHttpHeaders != null) {
			additionalHttpHeaders.forEach(headers::addAll);
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	/** OpenAI image model identifiers. */
	public enum ImageModel {

		GPT_IMAGE_1("gpt-image-1"), DALL_E_3("dall-e-3"), DALL_E_2("dall-e-2");

		private final String value;

		ImageModel(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}

	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record OpenAiImageRequest(@JsonProperty("prompt") String prompt,
			@JsonProperty("model") @Nullable String model, @JsonProperty("n") @Nullable Integer n,
			@JsonProperty("quality") @Nullable String quality,
			@JsonProperty("response_format") @Nullable String responseFormat,
			@JsonProperty("size") @Nullable String size, @JsonProperty("style") @Nullable String style,
			@JsonProperty("user") @Nullable String user) {

		public OpenAiImageRequest(String prompt, String model) {
			this(prompt, model, null, null, null, null, null, null);
		}

	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record OpenAiImageEditRequest(@JsonProperty("image") List<Media> image,
			@JsonProperty("prompt") String prompt, @JsonProperty("model") @Nullable String model,
			@JsonProperty("mask") @Nullable Media mask, @JsonProperty("n") @Nullable Integer n,
			@JsonProperty("quality") @Nullable String quality,
			@JsonProperty("response_format") @Nullable String responseFormat,
			@JsonProperty("size") @Nullable String size, @JsonProperty("user") @Nullable String user,
			@JsonIgnore @Nullable String imageFieldName) {

		public OpenAiImageEditRequest(List<Media> images, String prompt, String model) {
			this(images, prompt, model, null, null, null, null, null, null, null);
		}

		public OpenAiImageEditRequest(Media image, String prompt, String model) {
			this(List.of(image), prompt, model);
		}

		public static class Builder {

			private @Nullable List<Media> image;

			private @Nullable String prompt;

			private @Nullable String model;

			private @Nullable Media mask;

			private @Nullable Integer n;

			private @Nullable String quality;

			private @Nullable String responseFormat;

			private @Nullable String size;

			private @Nullable String user;

			private @Nullable String imageFieldName;

			public Builder image(List<Media> image) {
				this.image = image;
				return this;
			}

			public Builder prompt(String prompt) {
				this.prompt = prompt;
				return this;
			}

			public Builder model(@Nullable String model) {
				this.model = model;
				return this;
			}

			public Builder mask(@Nullable Media mask) {
				this.mask = mask;
				return this;
			}

			public Builder n(@Nullable Integer n) {
				this.n = n;
				return this;
			}

			public Builder quality(@Nullable String quality) {
				this.quality = quality;
				return this;
			}

			public Builder responseFormat(@Nullable String responseFormat) {
				this.responseFormat = responseFormat;
				return this;
			}

			public Builder size(@Nullable String size) {
				this.size = size;
				return this;
			}

			public Builder user(@Nullable String user) {
				this.user = user;
				return this;
			}

			public Builder imageFieldName(@Nullable String imageFieldName) {
				this.imageFieldName = imageFieldName;
				return this;
			}

			public OpenAiImageEditRequest build() {
				return new OpenAiImageEditRequest(Objects.requireNonNull(this.image, "image must be set"),
						Objects.requireNonNull(this.prompt, "prompt must be set"), this.model, this.mask, this.n,
						this.quality, this.responseFormat, this.size, this.user, this.imageFieldName);
			}

		}

	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record OpenAiImageResponse(@JsonProperty("created") @Nullable Long created,
			@JsonProperty("data") List<Data> data) {
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Data(@JsonProperty("url") @Nullable String url, @JsonProperty("b64_json") @Nullable String b64Json,
			@JsonProperty("revised_prompt") @Nullable String revisedPrompt) {
	}

	/** Builder for {@link OpenAiImageApi}. */
	public static final class Builder {

		private String baseUrl = OpenAiApiConstants.DEFAULT_BASE_URL;

		private @Nullable ApiKey apiKey;

		private MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

		private RestClient.Builder restClientBuilder = RestClient.builder();

		private ResponseErrorHandler responseErrorHandler = RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER;

		private String imagesPath = "v1/images/generations";

		private String imageEditPath = DEFAULT_IMAGE_EDIT_PATH;

		public Builder baseUrl(String baseUrl) {
			Assert.hasText(baseUrl, "baseUrl cannot be null or empty");
			this.baseUrl = baseUrl;
			return this;
		}

		public Builder imagesPath(String imagesPath) {
			Assert.hasText(imagesPath, "imagesPath cannot be null or empty");
			this.imagesPath = imagesPath;
			return this;
		}

		public Builder imageEditPath(String imageEditPath) {
			Assert.hasText(imageEditPath, "imageEditPath cannot be null or empty");
			this.imageEditPath = imageEditPath;
			return this;
		}

		public Builder apiKey(ApiKey apiKey) {
			Assert.notNull(apiKey, "apiKey cannot be null");
			this.apiKey = apiKey;
			return this;
		}

		public Builder apiKey(String apiKey) {
			Assert.notNull(apiKey, "apiKey cannot be null");
			this.apiKey = new SimpleApiKey(apiKey);
			return this;
		}

		public Builder headers(MultiValueMap<String, String> headers) {
			Assert.notNull(headers, "headers cannot be null");
			this.headers = headers;
			return this;
		}

		public Builder restClientBuilder(RestClient.Builder restClientBuilder) {
			Assert.notNull(restClientBuilder, "restClientBuilder cannot be null");
			this.restClientBuilder = restClientBuilder;
			return this;
		}

		public Builder responseErrorHandler(ResponseErrorHandler responseErrorHandler) {
			Assert.notNull(responseErrorHandler, "responseErrorHandler cannot be null");
			this.responseErrorHandler = responseErrorHandler;
			return this;
		}

		public OpenAiImageApi build() {
			Assert.notNull(this.apiKey, "apiKey must be set");
			return new OpenAiImageApi(this.baseUrl, Objects.requireNonNull(this.apiKey, "apiKey must be set"),
					this.headers, this.imagesPath, this.imageEditPath, this.restClientBuilder,
					this.responseErrorHandler);
		}

	}

}

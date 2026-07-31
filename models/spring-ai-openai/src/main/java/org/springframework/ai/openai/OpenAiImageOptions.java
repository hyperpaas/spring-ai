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

package org.springframework.ai.openai;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.openai.azure.AzureOpenAIServiceVersion;
import com.openai.core.MultipartField;
import com.openai.credential.Credential;
import com.openai.models.images.ImageEditParams;
import com.openai.models.images.ImageGenerateParams;
import com.openai.models.images.ImageModel;
import org.jspecify.annotations.Nullable;

import org.springframework.ai.content.Media;
import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * Configuration information for the Image Model implementation using the OpenAI Java SDK.
 *
 * @author Julien Dubois
 * @author Christian Tzolov
 * @author Mark Pollack
 */
public class OpenAiImageOptions extends AbstractOpenAiOptions implements ImageOptions {

	public static final String DEFAULT_IMAGE_MODEL = ImageModel.GPT_IMAGE_1_MINI.toString();

	public static final String DEFAULT_IMAGE_FIELD_NAME = "image[]";

	private static final String IMAGE_FIELD_NAME_HEADER = "X-Spring-Ai-Image-Field-Name";

	/**
	 * The number of images to generate. Must be between 1 and 10. For dall-e-3, only n=1
	 * is supported.
	 */
	private @Nullable Integer n;

	private @Nullable String model;

	/**
	 * The width of the generated images. Must be one of 256, 512, or 1024 for dall-e-2.
	 */
	private @Nullable Integer width;

	/**
	 * The height of the generated images. Must be one of 256, 512, or 1024 for dall-e-2.
	 */
	private @Nullable Integer height;

	/**
	 * The quality of the image that will be generated. hd creates images with finer
	 * details and greater consistency across the image. This param is only supported for
	 * dall-e-3. standard or hd
	 */
	private @Nullable String quality;

	/**
	 * The format in which the generated images are returned. Must be one of url or
	 * b64_json.
	 */
	private @Nullable String responseFormat;

	/**
	 * The size of the generated images. Must be one of 256x256, 512x512, or 1024x1024 for
	 * dall-e-2. Must be one of 1024x1024, 1792x1024, or 1024x1792 for dall-e-3 models.
	 */
	private @Nullable String size;

	/**
	 * The style of the generated images. Must be one of vivid or natural. Vivid causes
	 * the model to lean towards generating hyper-real and dramatic images. Natural causes
	 * the model to produce more natural, less hyper-real looking images. This param is
	 * only supported for dall-e-3. natural or vivid
	 */
	private @Nullable String style;

	/**
	 * A unique identifier representing your end-user, which can help OpenAI to monitor
	 * and detect abuse.
	 */
	private @Nullable String user;

	private @Nullable Resource mask;

	private @Nullable String imageFieldName;

	private @Nullable Map<String, String> httpHeaders;

	/**
	 * Create image options with the OpenAI defaults. This constructor is retained for
	 * compatibility with providers that populate options through JavaBean setters.
	 */
	public OpenAiImageOptions() {
		this(new Builder());
	}

	private OpenAiImageOptions(Builder builder) {
		this(builder.baseUrl, builder.apiKey, builder.credential, builder.model, builder.microsoftDeploymentName,
				builder.microsoftFoundryServiceVersion, builder.organizationId, builder.isMicrosoftFoundry,
				builder.isGitHubModels, builder.timeout, builder.maxRetries, builder.proxy, builder.customHeaders,
				builder.n, builder.width, builder.height, builder.quality, builder.responseFormat, builder.size,
				builder.style, builder.user);
		this.mask = builder.mask;
		this.imageFieldName = StringUtils.hasText(builder.imageFieldName) ? builder.imageFieldName
				: DEFAULT_IMAGE_FIELD_NAME;
		this.httpHeaders = builder.httpHeaders != null ? Map.copyOf(builder.httpHeaders) : null;
	}

	protected OpenAiImageOptions(@Nullable String baseUrl, @Nullable String apiKey, @Nullable Credential credential,
			@Nullable String model, @Nullable String microsoftDeploymentName,
			@Nullable AzureOpenAIServiceVersion microsoftFoundryServiceVersion, @Nullable String organizationId,
			@Nullable Boolean isMicrosoftFoundry, @Nullable Boolean isGitHubModels, @Nullable Duration timeout,
			@Nullable Integer maxRetries, @Nullable Proxy proxy, @Nullable Map<String, String> customHeaders,
			@Nullable Integer n, @Nullable Integer width, @Nullable Integer height, @Nullable String quality,
			@Nullable String responseFormat, @Nullable String size, @Nullable String style, @Nullable String user) {
		super(baseUrl, apiKey, credential, model != null ? model : DEFAULT_IMAGE_MODEL, microsoftDeploymentName,
				microsoftFoundryServiceVersion, organizationId, isMicrosoftFoundry, isGitHubModels, timeout, maxRetries,
				proxy, customHeaders);
		this.n = n;
		this.model = model != null ? model : DEFAULT_IMAGE_MODEL;
		this.width = width;
		this.height = height;
		this.quality = quality;
		this.responseFormat = responseFormat;
		this.size = size;
		this.style = style;
		this.user = user;
	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Create a copy of the supplied options.
	 * @param fromOptions the options to copy
	 * @return a new options instance
	 */
	public static OpenAiImageOptions fromOptions(OpenAiImageOptions fromOptions) {
		return new Builder(fromOptions).build();
	}

	/**
	 * Create a copy of this options instance.
	 * @return a new options instance
	 */
	public OpenAiImageOptions copy() {
		return fromOptions(this);
	}

	@Override
	public @Nullable Integer getN() {
		return this.n;
	}

	public void setN(@Nullable Integer n) {
		this.n = n;
	}

	@Override
	public @Nullable String getModel() {
		return this.model;
	}

	public void setModel(@Nullable String model) {
		this.model = model;
	}

	@Override
	public @Nullable Integer getWidth() {
		if (this.width != null) {
			return this.width;
		}
		return parseDimension(this.size, 0);
	}

	public void setWidth(@Nullable Integer width) {
		this.width = width;
		updateSizeFromDimensions();
	}

	@Override
	public @Nullable Integer getHeight() {
		if (this.height != null) {
			return this.height;
		}
		return parseDimension(this.size, 1);
	}

	public void setHeight(@Nullable Integer height) {
		this.height = height;
		updateSizeFromDimensions();
	}

	@Override
	public @Nullable String getResponseFormat() {
		return this.responseFormat;
	}

	public void setResponseFormat(@Nullable String responseFormat) {
		this.responseFormat = responseFormat;
	}

	public @Nullable String getSize() {
		if (this.size != null) {
			return this.size;
		}
		return (this.width != null && this.height != null) ? this.width + "x" + this.height : null;
	}

	public void setSize(@Nullable String size) {
		this.size = size;
		Integer parsedWidth = parseDimension(size, 0);
		Integer parsedHeight = parseDimension(size, 1);
		if (parsedWidth != null && parsedHeight != null) {
			this.width = parsedWidth;
			this.height = parsedHeight;
		}
	}

	public @Nullable String getUser() {
		return this.user;
	}

	public void setUser(@Nullable String user) {
		this.user = user;
	}

	public @Nullable String getQuality() {
		return this.quality;
	}

	public void setQuality(@Nullable String quality) {
		this.quality = quality;
	}

	@Override
	public @Nullable String getStyle() {
		return this.style;
	}

	public void setStyle(@Nullable String style) {
		this.style = style;
	}

	public @Nullable Resource getMask() {
		return this.mask;
	}

	public void setMask(@Nullable Resource mask) {
		this.mask = mask;
	}

	public @Nullable String getImageFieldName() {
		return this.imageFieldName;
	}

	public void setImageFieldName(@Nullable String imageFieldName) {
		this.imageFieldName = StringUtils.hasText(imageFieldName) ? imageFieldName : DEFAULT_IMAGE_FIELD_NAME;
	}

	@Override
	public @Nullable Map<String, String> getHttpHeaders() {
		return this.httpHeaders;
	}

	public void setHttpHeaders(@Nullable Map<String, String> httpHeaders) {
		this.httpHeaders = httpHeaders != null ? Map.copyOf(httpHeaders) : null;
	}

	private void updateSizeFromDimensions() {
		if (this.width != null && this.height != null) {
			this.size = this.width + "x" + this.height;
		}
	}

	private static @Nullable Integer parseDimension(@Nullable String size, int index) {
		if (size == null) {
			return null;
		}
		String[] dimensions = size.split("x");
		if (dimensions.length != 2) {
			return null;
		}
		try {
			return Integer.valueOf(dimensions[index]);
		}
		catch (NumberFormatException ex) {
			return null;
		}
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		OpenAiImageOptions that = (OpenAiImageOptions) o;
		return Objects.equals(this.n, that.n) && Objects.equals(this.model, that.model)
				&& Objects.equals(this.width, that.width) && Objects.equals(this.height, that.height)
				&& Objects.equals(this.quality, that.quality)
				&& Objects.equals(this.responseFormat, that.responseFormat) && Objects.equals(this.size, that.size)
				&& Objects.equals(this.style, that.style) && Objects.equals(this.user, that.user)
				&& Objects.equals(this.mask, that.mask) && Objects.equals(this.imageFieldName, that.imageFieldName)
				&& Objects.equals(this.httpHeaders, that.httpHeaders);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.n, this.model, this.width, this.height, this.quality, this.responseFormat, this.size,
				this.style, this.user, this.mask, this.imageFieldName, this.httpHeaders);
	}

	@Override
	public String toString() {
		return "OpenAiImageOptions{" + "n=" + this.n + ", model='" + this.model + '\'' + ", width=" + this.width
				+ ", height=" + this.height + ", quality='" + this.quality + '\'' + ", responseFormat='"
				+ this.responseFormat + '\'' + ", size='" + this.size + '\'' + ", style='" + this.style + '\''
				+ ", user='" + this.user + '\'' + ", mask=" + this.mask + ", imageFieldName='" + this.imageFieldName
				+ '\'' + '}';
	}

	public ImageGenerateParams toOpenAiImageGenerateParams(ImagePrompt imagePrompt) {
		if (imagePrompt.getInstructions().isEmpty()) {
			throw new IllegalArgumentException("Image prompt instructions cannot be empty");
		}

		String prompt = imagePrompt.getInstructions().get(0).getText();
		ImageGenerateParams.Builder builder = ImageGenerateParams.builder().prompt(prompt);

		// Use deployment name if available (for Microsoft Foundry), otherwise use model
		// name
		if (this.getDeploymentName() != null) {
			builder.model(this.getDeploymentName());
		}
		else if (this.getModel() != null) {
			builder.model(this.getModel());
		}

		if (this.getN() != null) {
			builder.n(this.getN().longValue());
		}
		if (this.getQuality() != null) {
			builder.quality(ImageGenerateParams.Quality.of(this.getQuality().toLowerCase()));
		}
		if (this.getResponseFormat() != null) {
			builder.responseFormat(ImageGenerateParams.ResponseFormat.of(this.getResponseFormat().toLowerCase()));
		}
		if (this.getSize() != null) {
			builder.size(ImageGenerateParams.Size.of(this.getSize()));
		}
		if (this.getStyle() != null) {
			builder.style(ImageGenerateParams.Style.of(this.getStyle().toLowerCase()));
		}
		if (this.getUser() != null) {
			builder.user(this.getUser());
		}
		if (!CollectionUtils.isEmpty(this.getCustomHeaders())) {
			this.getCustomHeaders().forEach(builder::putAdditionalHeader);
		}
		if (!CollectionUtils.isEmpty(this.getHttpHeaders())) {
			this.getHttpHeaders().forEach(builder::putAdditionalHeader);
		}

		return builder.build();
	}

	/**
	 * Convert these options and an editing prompt to OpenAI SDK parameters.
	 * @param imagePrompt the image editing prompt
	 * @return the SDK editing parameters
	 */
	public ImageEditParams toOpenAiImageEditParams(ImagePrompt imagePrompt) {
		if (imagePrompt.getInstructions().isEmpty()) {
			throw new IllegalArgumentException("Image prompt instructions cannot be empty");
		}

		List<Media> images = imagePrompt.getInstructions().get(0).getImage();
		if (CollectionUtils.isEmpty(images)) {
			throw new IllegalArgumentException("Image editing requires at least one source image");
		}

		List<InputStream> imageStreams = images.stream()
			.map(Media::getDataAsByteArray)
			.map(ByteArrayInputStream::new)
			.map(InputStream.class::cast)
			.toList();
		Media firstImage = images.get(0);
		MultipartField<ImageEditParams.Image> image = MultipartField.<ImageEditParams.Image>builder()
			.value(ImageEditParams.Image.ofInputStreams(imageStreams))
			.contentType(firstImage.getMimeType().toString())
			.filename(resolveImageFilename(firstImage))
			.build();
		ImageEditParams.Builder builder = ImageEditParams.builder()
			.image(image)
			.prompt(imagePrompt.getInstructions().get(0).getText());

		if (getDeploymentName() != null) {
			builder.model(getDeploymentName());
		}
		else if (getModel() != null) {
			builder.model(getModel());
		}
		if (getMask() != null) {
			try {
				String maskFilename = resolveMaskFilename(getMask());
				MultipartField<InputStream> mask = MultipartField.<InputStream>builder()
					.value(getMask().getInputStream())
					.contentType("image/png")
					.filename(maskFilename)
					.build();
				builder.mask(mask);
			}
			catch (IOException ex) {
				throw new IllegalArgumentException("Failed to read the image edit mask", ex);
			}
		}
		if (getN() != null) {
			builder.n(getN().longValue());
		}
		if (getQuality() != null) {
			builder.quality(ImageEditParams.Quality.of(getQuality().toLowerCase()));
		}
		if (getResponseFormat() != null) {
			builder.responseFormat(ImageEditParams.ResponseFormat.of(getResponseFormat().toLowerCase()));
		}
		if (getSize() != null) {
			builder.size(getSize());
		}
		if (getUser() != null) {
			builder.user(getUser());
		}
		if (!CollectionUtils.isEmpty(getCustomHeaders())) {
			getCustomHeaders().forEach(builder::putAdditionalHeader);
		}
		if (!CollectionUtils.isEmpty(getHttpHeaders())) {
			getHttpHeaders().forEach(builder::putAdditionalHeader);
		}
		String imageFieldName = getImageFieldName();
		if (!DEFAULT_IMAGE_FIELD_NAME.equals(imageFieldName)) {
			Assert.hasText(imageFieldName, "Image field name must not be empty");
			Assert.isTrue(imageFieldName.indexOf('\r') < 0 && imageFieldName.indexOf('\n') < 0,
					"Image field name must not contain line breaks");
			builder.replaceAdditionalHeaders(IMAGE_FIELD_NAME_HEADER, imageFieldName);
		}

		return builder.build();
	}

	private static String resolveImageFilename(Media image) {
		String filename = image.getName();
		if (filename.contains(".")) {
			return filename;
		}
		return filename + "." + image.getMimeType().getSubtype();
	}

	private static String resolveMaskFilename(Resource mask) {
		String filename = StringUtils.hasText(mask.getFilename()) ? mask.getFilename() : "mask";
		return filename.contains(".") ? filename : filename + ".png";
	}

	public static final class Builder extends AbstractBuilder<OpenAiImageOptions, Builder> {

		private @Nullable Integer n;

		private @Nullable Integer width;

		private @Nullable Integer height;

		private @Nullable String quality;

		private @Nullable String responseFormat;

		private @Nullable String size;

		private @Nullable String style;

		private @Nullable String user;

		private @Nullable Resource mask;

		private @Nullable String imageFieldName;

		private @Nullable Map<String, String> httpHeaders;

		public Builder() {
		}

		public Builder(OpenAiImageOptions options) {
			from(options);
		}

		public Builder from(OpenAiImageOptions fromOptions) {
			// Parent class fields
			this.baseUrl = fromOptions.getBaseUrl();
			this.apiKey = fromOptions.getApiKey();
			this.credential = fromOptions.getCredential();
			this.model = fromOptions.getModel();
			this.microsoftDeploymentName = fromOptions.getDeploymentName();
			this.microsoftFoundryServiceVersion = fromOptions.getMicrosoftFoundryServiceVersion();
			this.organizationId = fromOptions.getOrganizationId();
			this.isMicrosoftFoundry = fromOptions.isMicrosoftFoundry();
			this.isGitHubModels = fromOptions.isGitHubModels();
			this.timeout = fromOptions.getTimeout();
			this.maxRetries = fromOptions.getMaxRetries();
			this.proxy = fromOptions.getProxy();
			this.customHeaders = fromOptions.getCustomHeaders();
			// Child class fields
			this.n = fromOptions.getN();
			this.width = fromOptions.getWidth();
			this.height = fromOptions.getHeight();
			this.quality = fromOptions.getQuality();
			this.responseFormat = fromOptions.getResponseFormat();
			this.size = fromOptions.getSize();
			this.style = fromOptions.getStyle();
			this.user = fromOptions.getUser();
			this.mask = fromOptions.getMask();
			this.imageFieldName = fromOptions.getImageFieldName();
			this.httpHeaders = fromOptions.getHttpHeaders();
			return this;
		}

		public Builder merge(@Nullable ImageOptions from) {
			if (from == null) {
				return this;
			}
			if (from.getModel() != null) {
				this.model = from.getModel();
			}
			if (from.getN() != null) {
				this.n = from.getN();
			}
			if (from.getWidth() != null) {
				this.width = from.getWidth();
			}
			if (from.getHeight() != null) {
				this.height = from.getHeight();
			}
			if (from.getResponseFormat() != null) {
				this.responseFormat = from.getResponseFormat();
			}
			if (from.getStyle() != null) {
				this.style = from.getStyle();
			}
			Map<String, String> httpHeaders = from.getHttpHeaders();
			if (httpHeaders != null && !httpHeaders.isEmpty()) {
				Map<String, String> merged = this.httpHeaders != null ? new HashMap<>(this.httpHeaders)
						: new HashMap<>();
				merged.putAll(httpHeaders);
				this.httpHeaders = merged;
			}
			if (from instanceof AbstractOpenAiOptions castFrom) {
				if (castFrom.getBaseUrl() != null) {
					this.baseUrl = castFrom.getBaseUrl();
				}
				if (castFrom.getApiKey() != null) {
					this.apiKey = castFrom.getApiKey();
				}
				if (castFrom.getCredential() != null) {
					this.credential = castFrom.getCredential();
				}
				if (castFrom.getDeploymentName() != null) {
					this.microsoftDeploymentName = castFrom.getDeploymentName();
				}
				if (castFrom.getMicrosoftFoundryServiceVersion() != null) {
					this.microsoftFoundryServiceVersion = castFrom.getMicrosoftFoundryServiceVersion();
				}
				if (castFrom.getOrganizationId() != null) {
					this.organizationId = castFrom.getOrganizationId();
				}
				this.isMicrosoftFoundry = castFrom.isMicrosoftFoundry();
				this.isGitHubModels = castFrom.isGitHubModels();
				this.timeout = castFrom.getTimeout();
				this.maxRetries = castFrom.getMaxRetries();
				if (castFrom.getProxy() != null) {
					this.proxy = castFrom.getProxy();
				}
				if (castFrom.getCustomHeaders() != null) {
					if (this.customHeaders == null) {
						this.customHeaders = new HashMap<>(castFrom.getCustomHeaders());
					}
					else {
						Map<String, String> merged = new HashMap<>(this.customHeaders);
						merged.putAll(castFrom.getCustomHeaders());
						this.customHeaders = merged;
					}
				}
			}
			if (from instanceof OpenAiImageOptions castFrom) {
				if (castFrom.getQuality() != null) {
					this.quality = castFrom.getQuality();
				}
				if (castFrom.getSize() != null) {
					this.size = castFrom.getSize();
				}
				if (castFrom.getUser() != null) {
					this.user = castFrom.getUser();
				}
				if (castFrom.getMask() != null) {
					this.mask = castFrom.getMask();
				}
				if (castFrom.getImageFieldName() != null) {
					this.imageFieldName = castFrom.getImageFieldName();
				}
			}
			return this;
		}

		public Builder n(@Nullable Integer n) {
			this.n = n;
			return this;
		}

		@Override
		public Builder model(@Nullable String model) {
			return super.model(model);
		}

		/**
		 * Compatibility alias for {@link #n(Integer)}.
		 * @param n the number of images
		 * @return this builder
		 */
		public Builder N(@Nullable Integer n) {
			return n(n);
		}

		public Builder responseFormat(@Nullable String responseFormat) {
			this.responseFormat = responseFormat;
			return this;
		}

		public Builder width(@Nullable Integer width) {
			this.width = width;
			if (this.width != null && this.height != null) {
				this.size = this.width + "x" + this.height;
			}
			return this;
		}

		public Builder height(@Nullable Integer height) {
			this.height = height;
			if (this.width != null && this.height != null) {
				this.size = this.width + "x" + this.height;
			}
			return this;
		}

		public Builder user(@Nullable String user) {
			this.user = user;
			return this;
		}

		public Builder style(@Nullable String style) {
			this.style = style;
			return this;
		}

		public Builder quality(@Nullable String quality) {
			this.quality = quality;
			return this;
		}

		public Builder size(@Nullable String size) {
			this.size = size;
			Integer parsedWidth = parseDimension(size, 0);
			Integer parsedHeight = parseDimension(size, 1);
			if (parsedWidth != null && parsedHeight != null) {
				this.width = parsedWidth;
				this.height = parsedHeight;
			}
			return this;
		}

		public Builder mask(@Nullable Resource mask) {
			this.mask = mask;
			return this;
		}

		public Builder imageFieldName(@Nullable String imageFieldName) {
			this.imageFieldName = imageFieldName;
			return this;
		}

		public Builder httpHeaders(@Nullable Map<String, String> httpHeaders) {
			this.httpHeaders = httpHeaders;
			return this;
		}

		@Override
		public OpenAiImageOptions build() {
			return new OpenAiImageOptions(this);
		}

	}

}

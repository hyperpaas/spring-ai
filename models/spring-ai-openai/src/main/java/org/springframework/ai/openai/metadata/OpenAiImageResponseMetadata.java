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

package org.springframework.ai.openai.metadata;

import java.util.Objects;

import com.openai.models.images.ImagesResponse;
import org.jspecify.annotations.Nullable;

import org.springframework.ai.image.ImageResponseMetadata;
import org.springframework.ai.usage.Usage;
import org.springframework.ai.usage.UsageInputTokensDetails;
import org.springframework.ai.usage.UsageOutputTokensDetails;
import org.springframework.util.Assert;

/**
 * Represents the metadata for image response using the OpenAI Java SDK.
 *
 * @author Julien Dubois
 */
public class OpenAiImageResponseMetadata extends ImageResponseMetadata {

	private final Long created;

	/**
	 * Creates a new OpenAiImageResponseMetadata.
	 * @param created the creation timestamp
	 */
	protected OpenAiImageResponseMetadata(Long created) {
		this(created, Usage.EMPTY);
	}

	/**
	 * Creates a new OpenAiImageResponseMetadata.
	 * @param created the creation timestamp
	 * @param usage the token usage
	 */
	protected OpenAiImageResponseMetadata(Long created, Usage usage) {
		super(created, usage);
		this.created = created;
	}

	/**
	 * Creates metadata from an ImagesResponse.
	 * @param imagesResponse the OpenAI images response
	 * @return the metadata instance
	 */
	public static OpenAiImageResponseMetadata from(ImagesResponse imagesResponse) {
		Assert.notNull(imagesResponse, "imagesResponse must not be null");
		Usage usage = imagesResponse.usage().map(OpenAiImageResponseMetadata::from).orElse(Usage.EMPTY);
		return new OpenAiImageResponseMetadata(imagesResponse.created(), usage);
	}

	private static Usage from(ImagesResponse.Usage nativeUsage) {
		var nativeInputDetails = nativeUsage.inputTokensDetails();
		UsageInputTokensDetails inputDetails = UsageInputTokensDetails.builder()
			.imageTokens(Math.toIntExact(nativeInputDetails.imageTokens()))
			.textTokens(Math.toIntExact(nativeInputDetails.textTokens()))
			.build();

		Usage.Builder usage = Usage.builder()
			.inputTokens(Math.toIntExact(nativeUsage.inputTokens()))
			.inputTokensDetails(inputDetails)
			.outputTokens(Math.toIntExact(nativeUsage.outputTokens()))
			.totalTokens(Math.toIntExact(nativeUsage.totalTokens()));

		nativeUsage.outputTokensDetails().ifPresent(nativeOutputDetails -> {
			UsageOutputTokensDetails outputDetails = UsageOutputTokensDetails.builder()
				.imageTokens(Math.toIntExact(nativeOutputDetails.imageTokens()))
				.textTokens(Math.toIntExact(nativeOutputDetails.textTokens()))
				.build();
			usage.outputTokensDetails(outputDetails);
		});

		return usage.build();
	}

	@Override
	public Long getCreated() {
		return this.created;
	}

	@Override
	public String toString() {
		return "OpenAiImageResponseMetadata{" + "created=" + this.created + ", usage=" + getUsage() + '}';
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof OpenAiImageResponseMetadata that)) {
			return false;
		}
		return Objects.equals(this.created, that.created) && Objects.equals(getUsage(), that.getUsage());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.created, getUsage());
	}

}

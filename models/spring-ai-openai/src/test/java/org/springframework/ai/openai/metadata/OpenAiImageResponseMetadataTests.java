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

import com.openai.models.images.ImagesResponse;
import org.junit.jupiter.api.Test;

import org.springframework.ai.usage.Usage;

import static org.assertj.core.api.Assertions.assertThat;

class OpenAiImageResponseMetadataTests {

	@Test
	void missingNativeUsageMapsToEmptyUsage() {
		ImagesResponse response = ImagesResponse.builder().created(123L).build();

		OpenAiImageResponseMetadata metadata = OpenAiImageResponseMetadata.from(response);

		assertThat(metadata.getCreated()).isEqualTo(123L);
		assertThat(metadata.getUsage()).isSameAs(Usage.EMPTY);
	}

	@Test
	void mapsNativeUsageAndModalityDetails() {
		ImagesResponse.Usage.InputTokensDetails inputDetails = ImagesResponse.Usage.InputTokensDetails.builder()
			.imageTokens(11L)
			.textTokens(12L)
			.build();
		ImagesResponse.Usage.OutputTokensDetails outputDetails = ImagesResponse.Usage.OutputTokensDetails.builder()
			.imageTokens(21L)
			.textTokens(22L)
			.build();
		ImagesResponse.Usage nativeUsage = ImagesResponse.Usage.builder()
			.inputTokens(23L)
			.inputTokensDetails(inputDetails)
			.outputTokens(43L)
			.outputTokensDetails(outputDetails)
			.totalTokens(66L)
			.build();
		ImagesResponse response = ImagesResponse.builder().created(123L).usage(nativeUsage).build();

		OpenAiImageResponseMetadata metadata = OpenAiImageResponseMetadata.from(response);

		assertThat(metadata.getUsage().getInputTokens()).isEqualTo(23);
		assertThat(metadata.getUsage().getInputTokensDetails().getImageTokens()).isEqualTo(11);
		assertThat(metadata.getUsage().getInputTokensDetails().getTextTokens()).isEqualTo(12);
		assertThat(metadata.getUsage().getOutputTokens()).isEqualTo(43);
		assertThat(metadata.getUsage().getOutputTokensDetails().getImageTokens()).isEqualTo(21);
		assertThat(metadata.getUsage().getOutputTokensDetails().getTextTokens()).isEqualTo(22);
		assertThat(metadata.getUsage().getTotalTokens()).isEqualTo(66);
	}

}

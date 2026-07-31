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

package org.springframework.ai.image;

import org.junit.jupiter.api.Test;

import org.springframework.ai.usage.Usage;

import static org.assertj.core.api.Assertions.assertThat;

class ImageResponseMetadataTests {

	@Test
	void constructorsWithoutUsageUseEmptyUsage() {
		ImageResponseMetadata metadata = new ImageResponseMetadata(123L);

		assertThat(metadata.getCreated()).isEqualTo(123L);
		assertThat(metadata.getUsage()).isSameAs(Usage.EMPTY);
	}

	@Test
	void constructorsWithUsageExposeUsage() {
		Usage usage = Usage.builder().inputTokens(10).outputTokens(20).totalTokens(30).build();

		ImageResponseMetadata metadata = new ImageResponseMetadata(123L, usage);

		assertThat(metadata.getCreated()).isEqualTo(123L);
		assertThat(metadata.getUsage()).isSameAs(usage);
	}

}

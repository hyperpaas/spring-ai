/*
 * Copyright 2023-2025 the original author or authors.
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

import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.ai.content.Media;

import static org.assertj.core.api.Assertions.assertThat;

class ImagePromptTests {

	@Test
	void shouldCreateImageMessageWithInputImages() {
		Media image = Media.builder().mimeType(Media.Format.IMAGE_PNG).data(new byte[] { 1, 2, 3 }).build();

		ImageMessage imageMessage = new ImageMessage("Edit this image", List.of(image));

		assertThat(imageMessage.getText()).isEqualTo("Edit this image");
		assertThat(imageMessage.getImage()).containsExactly(image);
	}

	@Test
	void shouldCreateImagePromptWithInputImages() {
		Media image = Media.builder().mimeType(Media.Format.IMAGE_PNG).data(new byte[] { 1, 2, 3 }).build();
		ImageOptions imageOptions = ImageOptionsBuilder.builder().model("gpt-image-1").build();

		ImagePrompt imagePrompt = new ImagePrompt(List.of(image), "Edit this image", imageOptions);

		assertThat(imagePrompt.getInstructions()).hasSize(1);
		assertThat(imagePrompt.getInstructions().get(0).getText()).isEqualTo("Edit this image");
		assertThat(imagePrompt.getInstructions().get(0).getImage()).containsExactly(image);
		assertThat(imagePrompt.getOptions()).isEqualTo(imageOptions);
	}

}

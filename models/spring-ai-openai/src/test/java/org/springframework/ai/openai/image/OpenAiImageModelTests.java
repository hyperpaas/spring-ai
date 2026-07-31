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

import com.openai.client.OpenAIClient;
import com.openai.models.images.ImageEditParams;
import com.openai.models.images.ImageGenerateParams;
import com.openai.models.images.ImagesResponse;
import com.openai.services.blocking.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.ai.content.Media;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OpenAiImageModelTests {

	private OpenAIClient client;

	private ImageService imageService;

	private OpenAiImageModel imageModel;

	@BeforeEach
	void setUp() {
		this.client = mock(OpenAIClient.class);
		this.imageService = mock(ImageService.class);
		when(this.client.images()).thenReturn(this.imageService);
		this.imageModel = new OpenAiImageModel(this.client, OpenAiImageOptions.builder().build());
	}

	@Test
	void promptWithoutSourceImageCallsGenerate() {
		when(this.imageService.generate(any(ImageGenerateParams.class))).thenReturn(imageResponse());

		var response = this.imageModel.call(new ImagePrompt("draw a flower"));

		assertThat(response.getResult().getOutput().getB64Json()).isEqualTo("aW1hZ2U=");
		verify(this.imageService).generate(any(ImageGenerateParams.class));
		verify(this.imageService, never()).edit(any(ImageEditParams.class));
	}

	@Test
	void promptWithSourceImageCallsEdit() {
		when(this.imageService.edit(any(ImageEditParams.class))).thenReturn(imageResponse());
		Media sourceImage = Media.builder().mimeType(Media.Format.IMAGE_PNG).data(new byte[] { 1, 2, 3 }).build();

		var response = this.imageModel.call(new ImagePrompt(List.of(sourceImage), "add flowers"));

		assertThat(response.getResult().getOutput().getB64Json()).isEqualTo("aW1hZ2U=");
		verify(this.imageService).edit(any(ImageEditParams.class));
		verify(this.imageService, never()).generate(any(ImageGenerateParams.class));
	}

	private static ImagesResponse imageResponse() {
		com.openai.models.images.Image image = com.openai.models.images.Image.builder().b64Json("aW1hZ2U=").build();
		return ImagesResponse.builder().created(123L).addData(image).build();
	}

}

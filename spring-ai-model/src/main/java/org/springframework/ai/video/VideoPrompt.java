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

package org.springframework.ai.video;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

import org.springframework.ai.model.ModelRequest;

/**
 * A request to a video generation model.
 *
 * @author peatboy
 * @since 2.0.0
 */
public class VideoPrompt implements ModelRequest<List<VideoMessage>> {

	private final List<VideoMessage> messages;

	private @Nullable VideoOptions videoOptions;

	public VideoPrompt(List<VideoMessage> messages) {
		this.messages = messages;
	}

	public VideoPrompt(List<VideoMessage> messages, VideoOptions videoOptions) {
		this.messages = messages;
		this.videoOptions = videoOptions;
	}

	public VideoPrompt(VideoMessage message, VideoOptions options) {
		this(Collections.singletonList(message), options);
	}

	public VideoPrompt(String instructions, VideoOptions options) {
		this(new VideoMessage(instructions), options);
	}

	public VideoPrompt(String instructions) {
		this(new VideoMessage(instructions), VideoOptionsBuilder.builder().build());
	}

	@Override
	public List<VideoMessage> getInstructions() {
		return this.messages;
	}

	@Override
	public @Nullable VideoOptions getOptions() {
		return this.videoOptions;
	}

	@Override
	public String toString() {
		return "VideoPrompt{" + "messages=" + this.messages + ", videoOptions=" + this.videoOptions + '}';
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof VideoPrompt that)) {
			return false;
		}
		return Objects.equals(this.messages, that.messages) && Objects.equals(this.videoOptions, that.videoOptions);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.messages, this.videoOptions);
	}

}

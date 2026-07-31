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

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

import org.springframework.ai.content.Media;

/**
 * A video generation prompt and provider-specific media resources.
 *
 * @author peatboy
 * @since 2.0.0
 */
public class VideoMessage {

	private final String prompt;

	private @Nullable Map<String, List<Media>> resources;

	public VideoMessage(String prompt) {
		this.prompt = prompt;
	}

	public VideoMessage(String prompt, Map<String, List<Media>> resources) {
		this.prompt = prompt;
		this.resources = resources;
	}

	public String getPrompt() {
		return this.prompt;
	}

	public @Nullable Map<String, List<Media>> getResources() {
		return this.resources;
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof VideoMessage that)) {
			return false;
		}
		return Objects.equals(this.prompt, that.prompt);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.prompt);
	}

}

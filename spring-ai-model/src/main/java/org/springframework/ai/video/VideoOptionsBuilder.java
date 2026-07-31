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

import java.util.HashMap;
import java.util.Map;

import org.jspecify.annotations.Nullable;

/**
 * Builder for portable {@link VideoOptions}.
 *
 * @author peatboy
 * @since 2.0.0
 */
public final class VideoOptionsBuilder {

	private final DefaultVideoModelOptions options = new DefaultVideoModelOptions();

	private VideoOptionsBuilder() {
	}

	public static VideoOptionsBuilder builder() {
		return new VideoOptionsBuilder();
	}

	public VideoOptionsBuilder model(String model) {
		this.options.setModel(model);
		return this;
	}

	public VideoOptionsBuilder duration(Integer duration) {
		this.options.setDuration(duration);
		return this;
	}

	public VideoOptionsBuilder ratio(String ratio) {
		this.options.setRatio(ratio);
		return this;
	}

	public VideoOptionsBuilder resolution(String resolution) {
		this.options.setResolution(resolution);
		return this;
	}

	public VideoOptionsBuilder httpHeaders(Map<String, String> httpHeaders) {
		this.options.setHttpHeaders(httpHeaders);
		return this;
	}

	public VideoOptions build() {
		return this.options;
	}

	private static class DefaultVideoModelOptions implements VideoOptions {

		private @Nullable String model;

		private @Nullable Integer duration;

		private @Nullable String ratio;

		private @Nullable String resolution;

		private Map<String, String> httpHeaders = new HashMap<>();

		@Override
		public @Nullable String getModel() {
			return this.model;
		}

		@Override
		public @Nullable Integer getDuration() {
			return this.duration;
		}

		@Override
		public @Nullable String getRatio() {
			return this.ratio;
		}

		@Override
		public @Nullable String getResolution() {
			return this.resolution;
		}

		@Override
		public Map<String, String> getHttpHeaders() {
			return this.httpHeaders;
		}

		public void setModel(String model) {
			this.model = model;
		}

		public void setDuration(Integer duration) {
			this.duration = duration;
		}

		public void setRatio(String ratio) {
			this.ratio = ratio;
		}

		public void setResolution(String resolution) {
			this.resolution = resolution;
		}

		public void setHttpHeaders(Map<String, String> httpHeaders) {
			this.httpHeaders = httpHeaders;
		}

	}

}

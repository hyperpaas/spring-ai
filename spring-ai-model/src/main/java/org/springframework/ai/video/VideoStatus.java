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

import org.jspecify.annotations.Nullable;

import org.springframework.ai.chat.metadata.DefaultUsage;

/**
 * Provider-neutral status of a video generation task.
 *
 * @author peatboy
 * @since 2.0.0
 */
public class VideoStatus {

	private @Nullable String id;

	private @Nullable String model;

	private @Nullable String status;

	private @Nullable DefaultUsage usage;

	private @Nullable String url;

	/**
	 * Create an empty status. This constructor is intentionally public for provider
	 * extensions.
	 */
	public VideoStatus() {
	}

	private VideoStatus(Builder builder) {
		setId(builder.id);
		setModel(builder.model);
		setStatus(builder.status);
		setUsage(builder.usage);
		setUrl(builder.url);
	}

	public @Nullable String getId() {
		return this.id;
	}

	public void setId(@Nullable String id) {
		this.id = id;
	}

	public @Nullable String getModel() {
		return this.model;
	}

	public void setModel(@Nullable String model) {
		this.model = model;
	}

	public @Nullable String getStatus() {
		return this.status;
	}

	public void setStatus(@Nullable String status) {
		this.status = status;
	}

	public @Nullable DefaultUsage getUsage() {
		return this.usage;
	}

	public void setUsage(@Nullable DefaultUsage usage) {
		this.usage = usage;
	}

	public @Nullable String getUrl() {
		return this.url;
	}

	public void setUrl(@Nullable String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "VideoStatus{" + "id='" + this.id + '\'' + ", model='" + this.model + '\'' + ", status='" + this.status
				+ '\'' + ", usage=" + this.usage + ", url='" + this.url + '\'' + '}';
	}

	public static final class Builder {

		private @Nullable String id;

		private @Nullable String model;

		private @Nullable String status;

		private @Nullable DefaultUsage usage;

		private @Nullable String url;

		private Builder() {
		}

		public static Builder builder() {
			return new Builder();
		}

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder model(String model) {
			this.model = model;
			return this;
		}

		public Builder status(String status) {
			this.status = status;
			return this;
		}

		public Builder usage(DefaultUsage usage) {
			this.usage = usage;
			return this;
		}

		public Builder url(String url) {
			this.url = url;
			return this;
		}

		public VideoStatus build() {
			return new VideoStatus(this);
		}

	}

}

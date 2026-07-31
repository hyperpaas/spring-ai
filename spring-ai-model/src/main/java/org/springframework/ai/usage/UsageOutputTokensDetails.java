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

package org.springframework.ai.usage;

import java.util.Objects;

import org.jspecify.annotations.Nullable;

/**
 * Per-modality output token usage.
 *
 * @author peatboy
 * @since 2.0.0
 */
public class UsageOutputTokensDetails {

	private final @Nullable Integer imageTokens;

	private final @Nullable Integer textTokens;

	private final @Nullable Integer audioTokens;

	private final @Nullable Integer videoTokens;

	private UsageOutputTokensDetails(Builder builder) {
		this.imageTokens = builder.imageTokens;
		this.textTokens = builder.textTokens;
		this.audioTokens = builder.audioTokens;
		this.videoTokens = builder.videoTokens;
	}

	public static Builder builder() {
		return new Builder();
	}

	public @Nullable Integer getImageTokens() {
		return this.imageTokens;
	}

	public @Nullable Integer getTextTokens() {
		return this.textTokens;
	}

	public @Nullable Integer getAudioTokens() {
		return this.audioTokens;
	}

	public @Nullable Integer getVideoTokens() {
		return this.videoTokens;
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof UsageOutputTokensDetails that)) {
			return false;
		}
		return Objects.equals(this.imageTokens, that.imageTokens) && Objects.equals(this.textTokens, that.textTokens)
				&& Objects.equals(this.audioTokens, that.audioTokens)
				&& Objects.equals(this.videoTokens, that.videoTokens);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.imageTokens, this.textTokens, this.audioTokens, this.videoTokens);
	}

	@Override
	public String toString() {
		return "UsageOutputTokensDetails{" + "imageTokens=" + this.imageTokens + ", textTokens=" + this.textTokens
				+ ", audioTokens=" + this.audioTokens + ", videoTokens=" + this.videoTokens + '}';
	}

	public static class Builder {

		private @Nullable Integer imageTokens;

		private @Nullable Integer textTokens;

		private @Nullable Integer audioTokens;

		private @Nullable Integer videoTokens;

		private Builder() {
		}

		public Builder imageTokens(Integer imageTokens) {
			this.imageTokens = imageTokens;
			return this;
		}

		public Builder textTokens(Integer textTokens) {
			this.textTokens = textTokens;
			return this;
		}

		public Builder audioTokens(Integer audioTokens) {
			this.audioTokens = audioTokens;
			return this;
		}

		public Builder videoTokens(Integer videoTokens) {
			this.videoTokens = videoTokens;
			return this;
		}

		public UsageOutputTokensDetails build() {
			return new UsageOutputTokensDetails(this);
		}

	}

}

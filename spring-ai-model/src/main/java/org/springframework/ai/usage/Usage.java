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
 * Token usage details shared by multimodal model responses.
 *
 * @author peatboy
 * @since 2.0.0
 */
public class Usage {

	public static final Usage EMPTY = builder().build();

	private final @Nullable Integer inputTokens;

	private final @Nullable UsageInputTokensDetails inputTokensDetails;

	private final @Nullable Integer outputTokens;

	private final @Nullable UsageOutputTokensDetails outputTokensDetails;

	private final @Nullable Integer cacheTokens;

	private final @Nullable Integer totalTokens;

	private Usage(Builder builder) {
		this.inputTokens = builder.inputTokens;
		this.inputTokensDetails = builder.inputTokensDetails;
		this.outputTokens = builder.outputTokens;
		this.cacheTokens = builder.cacheTokens;
		this.totalTokens = builder.totalTokens;
		this.outputTokensDetails = builder.outputTokensDetails;
	}

	public static Builder builder() {
		return new Builder();
	}

	public @Nullable Integer getInputTokens() {
		return this.inputTokens;
	}

	public @Nullable UsageInputTokensDetails getInputTokensDetails() {
		return this.inputTokensDetails;
	}

	public @Nullable Integer getOutputTokens() {
		return this.outputTokens;
	}

	public @Nullable Integer getCacheTokens() {
		return this.cacheTokens;
	}

	public @Nullable Integer getTotalTokens() {
		return this.totalTokens;
	}

	public @Nullable UsageOutputTokensDetails getOutputTokensDetails() {
		return this.outputTokensDetails;
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Usage usage)) {
			return false;
		}
		return Objects.equals(this.inputTokens, usage.inputTokens)
				&& Objects.equals(this.inputTokensDetails, usage.inputTokensDetails)
				&& Objects.equals(this.outputTokens, usage.outputTokens)
				&& Objects.equals(this.cacheTokens, usage.cacheTokens)
				&& Objects.equals(this.totalTokens, usage.totalTokens)
				&& Objects.equals(this.outputTokensDetails, usage.outputTokensDetails);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.inputTokens, this.inputTokensDetails, this.outputTokens, this.cacheTokens,
				this.totalTokens, this.outputTokensDetails);
	}

	@Override
	public String toString() {
		return "Usage{" + "inputTokens=" + this.inputTokens + ", inputTokensDetails=" + this.inputTokensDetails
				+ ", outputTokens=" + this.outputTokens + ", totalTokens=" + this.totalTokens + ", cacheTokens="
				+ this.cacheTokens + ", outputTokensDetails=" + this.outputTokensDetails + '}';
	}

	public static class Builder {

		private @Nullable Integer inputTokens;

		private @Nullable UsageInputTokensDetails inputTokensDetails;

		private @Nullable Integer outputTokens;

		private @Nullable Integer cacheTokens;

		private @Nullable Integer totalTokens;

		private @Nullable UsageOutputTokensDetails outputTokensDetails;

		private Builder() {
		}

		public Builder inputTokens(Integer inputTokens) {
			this.inputTokens = inputTokens;
			return this;
		}

		public Builder inputTokensDetails(UsageInputTokensDetails inputTokensDetails) {
			this.inputTokensDetails = inputTokensDetails;
			return this;
		}

		public Builder outputTokens(Integer outputTokens) {
			this.outputTokens = outputTokens;
			return this;
		}

		public Builder cacheTokens(Integer cacheTokens) {
			this.cacheTokens = cacheTokens;
			return this;
		}

		public Builder totalTokens(Integer totalTokens) {
			this.totalTokens = totalTokens;
			return this;
		}

		public Builder outputTokensDetails(UsageOutputTokensDetails outputTokensDetails) {
			this.outputTokensDetails = outputTokensDetails;
			return this;
		}

		public Usage build() {
			return new Usage(this);
		}

	}

}

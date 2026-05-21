package org.springframework.ai.usage;

import java.util.Objects;

/**
 * 用量消耗
 *
 * @author peatboy 2026/04/29
 **/
public class Usage {

	public static final Usage EMPTY = builder().build();

	private final Integer inputTokens;

	private final UsageInputTokensDetails inputTokensDetails;

	private final Integer outputTokens;

	private final UsageOutputTokensDetails outputTokensDetails;

	private final Integer cacheTokens;

	private final Integer totalTokens;

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

	public Integer getInputTokens() {
		return inputTokens;
	}

	public UsageInputTokensDetails getInputTokensDetails() {
		return inputTokensDetails;
	}

	public Integer getOutputTokens() {
		return outputTokens;
	}

	public Integer getCacheTokens() {
		return cacheTokens;
	}

	public Integer getTotalTokens() {
		return totalTokens;
	}

	public UsageOutputTokensDetails getOutputTokensDetails() {
		return outputTokensDetails;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Usage usage = (Usage) o;
		return Objects.equals(inputTokens, usage.inputTokens)
				&& Objects.equals(inputTokensDetails, usage.inputTokensDetails)
				&& Objects.equals(outputTokens, usage.outputTokens) && Objects.equals(cacheTokens, usage.cacheTokens)
				&& Objects.equals(totalTokens, usage.totalTokens)
				&& Objects.equals(outputTokensDetails, usage.outputTokensDetails);
	}

	@Override
	public int hashCode() {
		return Objects.hash(inputTokens, inputTokensDetails, outputTokens, cacheTokens, totalTokens,
				outputTokensDetails);
	}

	@Override
	public String toString() {
		return "Usage{" + "inputTokens=" + inputTokens + ", inputTokensDetails=" + inputTokensDetails
				+ ", outputTokens=" + outputTokens + ", totalTokens=" + totalTokens + ", cacheTokens=" + cacheTokens
				+ ", outputTokensDetails=" + outputTokensDetails + '}';
	}

	public static class Builder {

		private Integer inputTokens;

		private UsageInputTokensDetails inputTokensDetails;

		private Integer outputTokens;

		private Integer cacheTokens;

		private Integer totalTokens;

		private UsageOutputTokensDetails outputTokensDetails;

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

package org.springframework.ai.usage;

import java.util.Objects;

/**
 * 输入 token 明细
 *
 * @author peatboy 2026/04/29
 */
public class UsageInputTokensDetails {

	private final Integer imageTokens;

	private final Integer textTokens;

	private final Integer audioTokens;

	private final Integer videoTokens;

	private UsageInputTokensDetails(Builder builder) {
		this.imageTokens = builder.imageTokens;
		this.textTokens = builder.textTokens;
		this.audioTokens = builder.audioTokens;
		this.videoTokens = builder.videoTokens;
	}

	public static Builder builder() {
		return new Builder();
	}

	public Integer getImageTokens() {
		return imageTokens;
	}

	public Integer getTextTokens() {
		return textTokens;
	}

	public Integer getAudioTokens() {
		return audioTokens;
	}

	public Integer getVideoTokens() {
		return videoTokens;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		UsageInputTokensDetails that = (UsageInputTokensDetails) o;
		return Objects.equals(imageTokens, that.imageTokens) && Objects.equals(textTokens, that.textTokens)
				&& Objects.equals(audioTokens, that.audioTokens) && Objects.equals(videoTokens, that.videoTokens);
	}

	@Override
	public int hashCode() {
		return Objects.hash(imageTokens, textTokens, audioTokens, videoTokens);
	}

	@Override
	public String toString() {
		return "UsageInputTokensDetails{" + "imageTokens=" + imageTokens + ", textTokens=" + textTokens
				+ ", audioTokens=" + audioTokens + ", videoTokens=" + videoTokens + '}';
	}

	public static class Builder {

		private Integer imageTokens;

		private Integer textTokens;

		private Integer audioTokens;

		private Integer videoTokens;

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

		public UsageInputTokensDetails build() {
			return new UsageInputTokensDetails(this);
		}

	}

}

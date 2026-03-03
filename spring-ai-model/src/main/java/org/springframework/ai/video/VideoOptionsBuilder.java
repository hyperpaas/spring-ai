package org.springframework.ai.video;

import org.jetbrains.annotations.Nullable;

/**
 * @author peatboy 2026/03/03
 **/
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

	public VideoOptions build() {
		return this.options;
	}

	private static class DefaultVideoModelOptions implements VideoOptions {

		private String model;

		private Integer duration;

		private String ratio;

		private String resolution;

		@Nullable
		@Override
		public String getModel() {
			return this.model;
		}

		@Nullable
		@Override
		public Integer getDuration() {
			return this.duration;
		}

		@Nullable
		@Override
		public String getRatio() {
			return this.ratio;
		}

		@Nullable
		@Override
		public String getResolution() {
			return this.resolution;
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

	}

}

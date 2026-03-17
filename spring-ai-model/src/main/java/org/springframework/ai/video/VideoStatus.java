package org.springframework.ai.video;

import org.springframework.ai.chat.metadata.DefaultUsage;

/**
 * 统一的视频状态实体
 *
 * @author peatboy 2026/03/05
 **/
public class VideoStatus {

	private String id;

	private String model;

	private String status;

	private DefaultUsage usage;

	private String url;

	// 提供一个无参构造器，便于继承拓展
	public VideoStatus() {
	}

	private VideoStatus(Builder builder) {
		setId(builder.id);
		setModel(builder.model);
		setStatus(builder.status);
		setUsage(builder.usage);
		setUrl(builder.url);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public DefaultUsage getUsage() {
		return usage;
	}

	public void setUsage(DefaultUsage usage) {
		this.usage = usage;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public static final class Builder {

		private String id;

		private String model;

		private String status;

		private DefaultUsage usage;

		private String url;

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

	@Override
	public String toString() {
		return "VideoStatus{" + "id='" + id + '\'' + ", model='" + model + '\'' + ", status='" + status + '\''
				+ ", usage=" + usage + ", url='" + url + '\'' + '}';
	}

}

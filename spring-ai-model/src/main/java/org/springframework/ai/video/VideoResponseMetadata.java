package org.springframework.ai.video;

import org.springframework.ai.model.MutableResponseMetadata;
import org.springframework.ai.usage.Usage;

/**
 * 各 provider 提供的 metadata 不一致，这里不给出标准字段
 *
 * @author peatboy 2026/03/03
 **/
public class VideoResponseMetadata extends MutableResponseMetadata {

	private final Usage usage;

	public VideoResponseMetadata() {
		this.usage = Usage.EMPTY;
	}

	public VideoResponseMetadata(Usage usage) {
		this.usage = usage;
	}

	public Usage getUsage() {
		return this.usage;
	}

}

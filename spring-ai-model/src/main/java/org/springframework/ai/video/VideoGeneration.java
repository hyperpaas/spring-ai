package org.springframework.ai.video;

import org.springframework.ai.model.ModelResult;
import org.springframework.ai.model.ResultMetadata;

/**
 * @author peatboy 2026/03/03
 **/
public class VideoGeneration implements ModelResult<Video> {

	private VideoGenerationMetadata videoGenerationMetadata;

	private Video video;

	public VideoGeneration(Video video) {
		this.video = video;
	}

	public VideoGeneration(VideoGenerationMetadata videoGenerationMetadata, Video video) {
		this.videoGenerationMetadata = videoGenerationMetadata;
		this.video = video;
	}

	@Override
	public Video getOutput() {
		return this.video;
	}

	@Override
	public ResultMetadata getMetadata() {
		return this.videoGenerationMetadata;
	}

	@Override
	public String toString() {
		return "VideoGeneration{" + "videoGenerationMetadata=" + videoGenerationMetadata + ", video=" + video + '}';
	}

}

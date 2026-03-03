package org.springframework.ai.video;

import java.util.List;
import java.util.Objects;
import org.springframework.ai.model.ModelResponse;
import org.springframework.ai.model.ResponseMetadata;
import org.springframework.util.CollectionUtils;

/**
 * @author peatboy 2026/03/03
 **/
public class VideoResponse implements ModelResponse<VideoGeneration> {

	private final VideoResponseMetadata videoResponseMetadata;

	private final List<VideoGeneration> videoGenerations;

	public VideoResponse(List<VideoGeneration> videoGenerations) {
		this(new VideoResponseMetadata(), videoGenerations);
	}

	public VideoResponse(VideoResponseMetadata videoResponseMetadata, List<VideoGeneration> videoGenerations) {
		this.videoResponseMetadata = videoResponseMetadata;
		this.videoGenerations = List.copyOf(videoGenerations);
	}

	@Override
	public VideoGeneration getResult() {
		if (CollectionUtils.isEmpty(this.videoGenerations)) {
			return null;
		}
		return this.videoGenerations.get(0);
	}

	@Override
	public List<VideoGeneration> getResults() {
		return this.videoGenerations;
	}

	@Override
	public ResponseMetadata getMetadata() {
		return this.videoResponseMetadata;
	}

	@Override
	public String toString() {
		return "VideoResponse{" + "videoResponseMetadata=" + videoResponseMetadata + ", videoGenerations="
				+ videoGenerations + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		VideoResponse that = (VideoResponse) o;
		return Objects.equals(videoResponseMetadata, that.videoResponseMetadata)
				&& Objects.equals(videoGenerations, that.videoGenerations);
	}

	@Override
	public int hashCode() {
		return Objects.hash(videoResponseMetadata, videoGenerations);
	}

}

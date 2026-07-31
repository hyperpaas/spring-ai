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

import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

import org.springframework.ai.model.ModelResponse;
import org.springframework.ai.model.ResponseMetadata;
import org.springframework.util.CollectionUtils;

/**
 * A response from a video generation model.
 *
 * @author peatboy
 * @since 2.0.0
 */
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
	public @Nullable VideoGeneration getResult() {
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
		return "VideoResponse{" + "videoResponseMetadata=" + this.videoResponseMetadata + ", videoGenerations="
				+ this.videoGenerations + '}';
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof VideoResponse that)) {
			return false;
		}
		return Objects.equals(this.videoResponseMetadata, that.videoResponseMetadata)
				&& Objects.equals(this.videoGenerations, that.videoGenerations);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.videoResponseMetadata, this.videoGenerations);
	}

}

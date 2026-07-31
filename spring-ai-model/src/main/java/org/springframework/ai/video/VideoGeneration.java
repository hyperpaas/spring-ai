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

import org.springframework.ai.model.ModelResult;
import org.springframework.ai.model.ResultMetadata;

/**
 * A video generation result.
 *
 * @author peatboy
 * @since 2.0.0
 */
public class VideoGeneration implements ModelResult<Video> {

	private static final VideoGenerationMetadata NONE = new VideoGenerationMetadata() {

	};

	private final VideoGenerationMetadata videoGenerationMetadata;

	private final Video video;

	public VideoGeneration(Video video) {
		this.videoGenerationMetadata = NONE;
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
		return "VideoGeneration{" + "videoGenerationMetadata=" + this.videoGenerationMetadata + ", video=" + this.video
				+ '}';
	}

}

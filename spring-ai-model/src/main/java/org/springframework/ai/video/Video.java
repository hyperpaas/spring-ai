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

import java.util.Objects;

import org.jspecify.annotations.Nullable;
import reactor.core.publisher.Flux;

/**
 * A generated video task and its status stream.
 *
 * @author peatboy
 * @since 2.0.0
 */
public class Video {

	private String id;

	private @Nullable Flux<VideoStatus> statusFlux;

	public Video(String id) {
		this.id = id;
	}

	public Video(String id, Flux<VideoStatus> statusFlux) {
		this.id = id;
		this.statusFlux = statusFlux;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public @Nullable Flux<VideoStatus> getStatusFlux() {
		return this.statusFlux;
	}

	public void setStatusFlux(Flux<VideoStatus> statusFlux) {
		this.statusFlux = statusFlux;
	}

	@Override
	public String toString() {
		return "Video{" + "id='" + this.id + '\'' + '}';
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id);
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Video video)) {
			return false;
		}
		return Objects.equals(this.id, video.id);
	}

}

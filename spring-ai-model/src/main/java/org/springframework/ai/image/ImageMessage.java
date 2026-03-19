/*
 * Copyright 2023-2024 the original author or authors.
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

package org.springframework.ai.image;

import java.util.List;
import java.util.Objects;

import org.springframework.ai.content.Media;

public class ImageMessage {

	private String text;

	private Float weight;

	private List<Media> image;

	public ImageMessage(String text) {
		this.text = text;
	}

	public ImageMessage(String text, Float weight) {
		this.text = text;
		this.weight = weight;
	}

	public ImageMessage(String text, List<Media> image) {
		this.text = text;
		this.image = image;
	}

	public String getText() {
		return this.text;
	}

	public Float getWeight() {
		return this.weight;
	}

	public List<Media> getImage() {
		return this.image;
	}

	@Override
	public String toString() {
		return "ImageMessage{" + "text='" + this.text + '\'' + ", weight=" + this.weight + ", image=" + this.image
				+ '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ImageMessage that)) {
			return false;
		}
		return Objects.equals(this.text, that.text) && Objects.equals(this.weight, that.weight)
				&& Objects.equals(this.image, that.image);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.text, this.weight, this.image);
	}

}

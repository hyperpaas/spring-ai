package org.springframework.ai.video;

import java.util.Objects;

/**
 * @author peatboy 2026/03/03
 **/
public class Video {

	private String id;

	public Video(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Video{" + "id='" + id + '\'' + '}';
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id);
	}

}

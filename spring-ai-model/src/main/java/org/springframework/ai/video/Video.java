package org.springframework.ai.video;

import java.util.Objects;
import reactor.core.publisher.Flux;

/**
 * @author peatboy 2026/03/03
 **/
public class Video {

	private String id;

	private Flux<VideoStatus> statusFlux;

	public Video(String id) {
		this.id = id;
	}

	public Video(String id, Flux<VideoStatus> statusFlux) {
		this.id = id;
		this.statusFlux = statusFlux;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Flux<VideoStatus> getStatusFlux() {
		return statusFlux;
	}

	public void setStatusFlux(Flux<VideoStatus> statusFlux) {
		this.statusFlux = statusFlux;
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

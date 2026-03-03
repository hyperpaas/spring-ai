package org.springframework.ai.video;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.ai.model.ModelRequest;

/**
 * @author peatboy 2026/03/03
 **/
public class VideoPrompt implements ModelRequest<List<VideoMessage>> {

	private final List<VideoMessage> messages;

	private VideoOptions videoOptions;

	public VideoPrompt(List<VideoMessage> messages) {
		this.messages = messages;
	}

	public VideoPrompt(List<VideoMessage> messages, VideoOptions videoOptions) {
		this.messages = messages;
		this.videoOptions = videoOptions;
	}

	public VideoPrompt(VideoMessage message, VideoOptions options) {
		this(Collections.singletonList(message), options);
	}

	public VideoPrompt(String instructions, VideoOptions options) {
		this(new VideoMessage(instructions), options);
	}

	public VideoPrompt(String instructions) {
		this(new VideoMessage(instructions), VideoOptionsBuilder.builder().build());
	}

	@Override
	public List<VideoMessage> getInstructions() {
		return this.messages;
	}

	@Override
	public VideoOptions getOptions() {
		return this.videoOptions;
	}

	@Override
	public String toString() {
		return "VideoPrompt{" + "messages=" + messages + ", videoOptions=" + videoOptions + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		VideoPrompt that = (VideoPrompt) o;
		return Objects.equals(messages, that.messages) && Objects.equals(videoOptions, that.videoOptions);
	}

	@Override
	public int hashCode() {
		return Objects.hash(messages, videoOptions);
	}

}

package org.springframework.ai.video;

import java.util.Map;
import java.util.Objects;
import org.springframework.ai.content.Media;

/**
 * @author peatboy 2026/03/03
 **/
public class VideoMessage {

	private String prompt;

	/**
	 * 指代本次视频生成中使用的外部资源，由于各平台资源引用方式不同，这里统一存储为 Map，资源由 key 自行索引
	 */
	private Map<String, Media> resources;

	public VideoMessage(String prompt) {
		this.prompt = prompt;
	}

	public VideoMessage(String prompt, Map<String, Media> resources) {
		this.prompt = prompt;
		this.resources = resources;
	}

	public String getPrompt() {
		return prompt;
	}

	public Map<String, Media> getResources() {
		return resources;
	}

	@Override
	public boolean equals(Object o) {

		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		VideoMessage that = (VideoMessage) o;
		return Objects.equals(prompt, that.prompt);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(prompt);
	}

}

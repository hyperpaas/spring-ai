package org.springframework.ai.video;

import java.util.Map;
import org.springframework.ai.model.Model;

/**
 * @author peatboy 2026/03/03
 **/
@FunctionalInterface
public interface VideoModel extends Model<VideoPrompt, VideoResponse> {

	VideoResponse call(VideoPrompt request);

	default VideoResponse call(VideoPrompt request, Map<String, Object> context) {
		// 默认忽略 context
		return call(request);
	}

}

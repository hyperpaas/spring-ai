package org.springframework.ai.video;

import org.springframework.ai.model.Model;

/**
 * @author peatboy 2026/03/03
 **/
@FunctionalInterface
public interface VideoModel extends Model<VideoPrompt, VideoResponse> {

	VideoResponse call(VideoPrompt request);

}

package org.springframework.ai.video;

import java.util.Map;
import org.springframework.ai.model.ModelOptions;
import org.springframework.lang.Nullable;

/**
 * 视频生成参数在各平台的实现差异巨大，这里只提供最常见的通用选项
 *
 * @author peatboy 2026/03/03
 **/
public interface VideoOptions extends ModelOptions {

	@Nullable
	String getModel();

	@Nullable
	Integer getDuration();

	@Nullable
	String getRatio();

	@Nullable
	String getResolution();

	@Nullable
	Map<String, String> getHttpHeaders();

}

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

package org.springframework.ai.openai.chat;

import java.util.Map;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.Test;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;

import static org.assertj.core.api.Assertions.assertThat;

class OpenAiChatModelHttpHeadersTests {

	private static final String CHAT_COMPLETION_RESPONSE = """
			{"id":"chatcmpl-test","object":"chat.completion","created":1700000000,"model":"gpt-4","choices":[{"index":0,"message":{"role":"assistant","content":"Hello"},"finish_reason":"stop"}],"usage":{"prompt_tokens":5,"completion_tokens":1,"total_tokens":6}}
			""";

	@Test
	void runtimeHeadersAreSentAndOverrideDefaultHeaders() throws Exception {
		try (MockWebServer server = new MockWebServer()) {
			server.enqueue(new MockResponse().setResponseCode(200)
				.setHeader("Content-Type", "application/json")
				.setBody(CHAT_COMPLETION_RESPONSE));
			server.start();

			OpenAiChatOptions defaultOptions = OpenAiChatOptions.builder()
				.baseUrl(server.url("/v1").toString())
				.apiKey("test-key")
				.model("gpt-4")
				.httpHeaders(Map.of("X-Default-Header", "default", "X-Override-Header", "default"))
				.build();
			OpenAiChatModel model = OpenAiChatModel.builder().options(defaultOptions).build();
			OpenAiChatOptions runtimeOptions = OpenAiChatOptions.builder()
				.model("gpt-4")
				.httpHeaders(Map.of("X-Runtime-Header", "runtime", "X-Override-Header", "runtime"))
				.extraBody(Map.of("top_k", 50))
				.build();

			var response = model.call(new Prompt("Hi", runtimeOptions));

			assertThat(response.getResult().getOutput().getText()).isEqualTo("Hello");
			RecordedRequest request = server.takeRequest();
			assertThat(request.getPath()).isEqualTo("/v1/chat/completions");
			assertThat(request.getHeader("X-Default-Header")).isEqualTo("default");
			assertThat(request.getHeader("X-Runtime-Header")).isEqualTo("runtime");
			assertThat(request.getHeader("X-Override-Header")).isEqualTo("runtime");
			assertThat(request.getBody().readUtf8()).contains("\"top_k\":50").doesNotContain("extra_body");
		}
	}

}

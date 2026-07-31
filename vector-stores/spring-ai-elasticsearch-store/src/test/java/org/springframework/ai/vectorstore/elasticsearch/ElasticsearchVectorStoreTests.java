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

package org.springframework.ai.vectorstore.elasticsearch;

import java.util.Base64;

import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.transport.rest5_client.low_level.Rest5Client;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.node.ObjectNode;

import org.springframework.ai.content.Media;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.util.MimeType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;

class ElasticsearchVectorStoreTests {

	@Test
	void customJsonMapperRestoresLegacyArrayMedia() throws JacksonException {
		SimpleModule mediaModule = new SimpleModule();
		mediaModule.addDeserializer(Media.class, new LegacyMediaDeserializer());
		JsonMapper jsonMapper = JsonMapper.builder().addModule(mediaModule).build();
		ObjectNode source = (ObjectNode) jsonMapper.readTree("""
				{
					"id": "legacy-media",
					"media": [{
						"mimeType": "image/png",
						"data": "AQID",
						"name": "source-image"
					}],
					"metadata": {"origin": "legacy"}
				}
				""");
		Hit<ObjectNode> hit = Hit
			.of(builder -> builder.index("documents").id("legacy-media").source(source).score(1.0));
		ElasticsearchVectorStore vectorStore = ElasticsearchVectorStore
			.builder(mock(Rest5Client.class), mock(EmbeddingModel.class))
			.jsonMapper(jsonMapper)
			.build();

		Document document = vectorStore.toDocument(hit);

		assertThat(document.isText()).isFalse();
		assertThat(document.getMedia()).isNotNull();
		assertThat(document.getMedia().getMimeType()).isEqualTo(Media.Format.IMAGE_PNG);
		assertThat(document.getMedia().getName()).isEqualTo("source-image");
		assertThat(document.getMedia().getDataAsByteArray()).containsExactly((byte) 1, (byte) 2, (byte) 3);
		assertThat(document.getMetadata()).containsEntry("origin", "legacy");
	}

	@Test
	void builderRejectsNullJsonMapper() {
		ElasticsearchVectorStore.Builder builder = ElasticsearchVectorStore.builder(mock(Rest5Client.class),
				mock(EmbeddingModel.class));

		assertThatIllegalArgumentException().isThrownBy(() -> builder.jsonMapper(null))
			.withMessage("jsonMapper must not be null");
	}

	private static final class LegacyMediaDeserializer extends ValueDeserializer<Media> {

		@Override
		public Media deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
			JsonNode node = parser.readValueAsTree();
			if (node.isArray()) {
				node = node.get(0);
			}
			return Media.builder()
				.mimeType(MimeType.valueOf(node.get("mimeType").asString()))
				.data(Base64.getDecoder().decode(node.get("data").asString()))
				.name(node.get("name").asString())
				.build();
		}

	}

}

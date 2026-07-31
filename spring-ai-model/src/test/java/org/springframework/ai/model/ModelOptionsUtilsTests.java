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

package org.springframework.ai.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class ModelOptionsUtilsTests {

	@Test
	void jsonUtilitiesUseJackson3() {
		JsonMapper mapper = JsonMapper.builder().build();

		Map<String, Object> values = ModelOptionsUtils.jsonToMap("{\"name\":\"test\",\"count\":2}", mapper);

		assertThat(values).containsEntry("name", "test").containsEntry("count", 2);
		assertThat(ModelOptionsUtils.jsonToObject(ModelOptionsUtils.toJsonString(values), Map.class))
			.containsAllEntriesOf(values);
	}

	@Test
	void mergePreservesPropertiesExposedByJsonAnyGetter() {
		FlexibleOptions defaults = new FlexibleOptions();
		defaults.setName("default");
		defaults.setCount(1);
		defaults.setExtraBodyProperty("default_option", true);
		FlexibleOptions runtime = new FlexibleOptions();
		runtime.setName("runtime");
		runtime.setExtraBodyProperty("vendor_option", "enabled");

		FlexibleOptions merged = ModelOptionsUtils.merge(runtime, defaults, FlexibleOptions.class);

		assertThat(merged.getName()).isEqualTo("runtime");
		assertThat(merged.getCount()).isEqualTo(1);
		assertThat(merged.extraBody()).containsEntry("default_option", true).containsEntry("vendor_option", "enabled");
	}

	@Test
	void explicitAcceptedFieldsRestrictJsonAnyGetterProperties() {
		FlexibleOptions defaults = new FlexibleOptions();
		defaults.setCount(1);
		defaults.setExtraBodyProperty("default_option", true);

		FlexibleOptions merged = ModelOptionsUtils.merge(Map.of("name", "runtime", "vendor_option", "enabled"),
				defaults, FlexibleOptions.class, List.of("name", "count"));

		assertThat(merged.getName()).isEqualTo("runtime");
		assertThat(merged.getCount()).isEqualTo(1);
		assertThat(merged.extraBody()).isEmpty();
	}

	static final class FlexibleOptions implements ModelOptions {

		@JsonProperty("name")
		private String name;

		@JsonProperty("count")
		private Integer count;

		private final Map<String, Object> extraBody = new HashMap<>();

		String getName() {
			return this.name;
		}

		void setName(String name) {
			this.name = name;
		}

		Integer getCount() {
			return this.count;
		}

		void setCount(Integer count) {
			this.count = count;
		}

		@JsonAnyGetter
		Map<String, Object> extraBody() {
			return this.extraBody;
		}

		@JsonAnySetter
		void setExtraBodyProperty(String name, Object value) {
			this.extraBody.put(name, value);
		}

	}

}

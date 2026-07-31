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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

import org.springframework.ai.util.JacksonUtils;
import org.springframework.ai.util.json.schema.JsonSchemaUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.lang.Contract;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/**
 * Utility class for manipulating {@link ModelOptions} objects.
 *
 * @author Christian Tzolov
 * @author Thomas Vitale
 * @author chabinhwang
 * @author Sebastien Deleuze
 * @since 0.8.0
 */
public abstract class ModelOptionsUtils {

	/**
	 * Jackson mapper retained for compatibility with the 1.1.x public API.
	 */
	public static final JsonMapper OBJECT_MAPPER = JsonMapper.builder()
		.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
		.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
		.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
		.addModules(JacksonUtils.instantiateAvailableModules())
		.build();

	private static final List<String> BEAN_MERGE_FIELD_EXCLUSIONS = List.of("class");

	private static final ConcurrentHashMap<Class<?>, JsonPropertyResult> REQUEST_FIELD_NAMES_PER_CLASS = new ConcurrentHashMap<>();

	private static final TypeReference<HashMap<String, Object>> MAP_TYPE_REFERENCE = new TypeReference<>() {

	};

	/**
	 * Convert JSON to a map using the compatibility mapper.
	 * @param json the JSON input
	 * @return the converted map
	 */
	public static Map<String, Object> jsonToMap(String json) {
		return jsonToMap(json, OBJECT_MAPPER);
	}

	/**
	 * Convert JSON to a map using a supplied Jackson 3 mapper.
	 * @param json the JSON input
	 * @param objectMapper the mapper to use
	 * @return the converted map
	 */
	public static Map<String, Object> jsonToMap(String json, JsonMapper objectMapper) {
		try {
			return Objects.requireNonNull(objectMapper.readValue(json, MAP_TYPE_REFERENCE));
		}
		catch (JacksonException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Convert JSON to an object.
	 * @param json the JSON input
	 * @param type the target type
	 * @param <T> the target type
	 * @return the converted object
	 */
	public static <T> T jsonToObject(String json, Class<T> type) {
		try {
			return OBJECT_MAPPER.readValue(json, type);
		}
		catch (JacksonException ex) {
			throw new RuntimeException("Failed to parse json: " + json, ex);
		}
	}

	/**
	 * Serialize an object to JSON.
	 * @param object the object to serialize
	 * @return the JSON representation
	 */
	public static String toJsonString(Object object) {
		try {
			return OBJECT_MAPPER.writeValueAsString(object);
		}
		catch (JacksonException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Serialize an object to formatted JSON.
	 * @param object the object to serialize
	 * @return the formatted JSON representation
	 */
	public static String toJsonStringPrettyPrinter(Object object) {
		try {
			return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
		}
		catch (JacksonException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Merge non-null source properties into a target and convert the result.
	 * @param source the source object
	 * @param target the target object
	 * @param clazz the result type
	 * @param acceptedFieldNames explicitly accepted JSON field names
	 * @param <T> the result type
	 * @return the merged result
	 */
	public static <T> T merge(@Nullable Object source, @Nullable Object target, Class<T> clazz,
			@Nullable List<String> acceptedFieldNames) {
		if (source == null) {
			source = Map.of();
		}

		JsonPropertyResult propertyResult = REQUEST_FIELD_NAMES_PER_CLASS.computeIfAbsent(clazz,
				ModelOptionsUtils::getJsonPropertyResult);
		List<String> requestFieldNames = CollectionUtils.isEmpty(acceptedFieldNames) ? propertyResult.properties()
				: acceptedFieldNames;
		boolean acceptAllFields = CollectionUtils.isEmpty(acceptedFieldNames) && propertyResult.acceptAllFields();

		if (!acceptAllFields && CollectionUtils.isEmpty(requestFieldNames)) {
			throw new IllegalArgumentException("No @JsonProperty fields found in the " + clazz.getName());
		}

		Map<String, Object> sourceMap = objectToMap(source);
		Map<String, Object> targetMap = objectToMap(target);
		sourceMap.forEach((key, value) -> {
			if (value != null) {
				targetMap.put(key, value);
			}
		});
		targetMap.entrySet().removeIf(entry -> !acceptAllFields && !requestFieldNames.contains(entry.getKey()));
		return mapToClass(targetMap, clazz);
	}

	/**
	 * Merge non-null source properties into a target and convert the result.
	 * @param source the source object
	 * @param target the target object
	 * @param clazz the result type
	 * @param <T> the result type
	 * @return the merged result
	 */
	public static <T> T merge(@Nullable Object source, @Nullable Object target, Class<T> clazz) {
		return merge(source, target, clazz, null);
	}

	/**
	 * Convert an object to a map.
	 * @param source the source object
	 * @return the converted map containing non-null values
	 */
	public static Map<String, Object> objectToMap(@Nullable Object source) {
		if (source == null) {
			return new HashMap<>();
		}
		try {
			String json = OBJECT_MAPPER.writeValueAsString(source);
			Map<String, Object> values = OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {

			});
			return Objects.requireNonNull(values)
				.entrySet()
				.stream()
				.filter(entry -> entry.getValue() != null)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
		catch (JacksonException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Convert a map to a target class.
	 * @param source the source map
	 * @param clazz the target class
	 * @param <T> the target type
	 * @return the converted object
	 */
	public static <T> T mapToClass(Map<String, Object> source, Class<T> clazz) {
		try {
			String json = OBJECT_MAPPER.writeValueAsString(source);
			return OBJECT_MAPPER.readValue(json, clazz);
		}
		catch (JacksonException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Inspect a class for explicitly named or arbitrary JSON properties.
	 * @param clazz the class to inspect
	 * @return the accepted property description
	 */
	public static JsonPropertyResult getJsonPropertyResult(Class<?> clazz) {
		List<String> values = new ArrayList<>();
		for (Method method : clazz.getDeclaredMethods()) {
			if (method.getAnnotation(JsonAnyGetter.class) != null) {
				return new JsonPropertyResult(true, values);
			}
		}
		for (Field field : clazz.getDeclaredFields()) {
			JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
			if (jsonProperty != null) {
				values.add(jsonProperty.value());
			}
		}
		return new JsonPropertyResult(false, values);
	}

	/**
	 * Copy portable option properties to a provider-specific option type.
	 * @param sourceBean the source options
	 * @param sourceInterfaceClazz the portable options interface
	 * @param targetBeanClazz the provider-specific options type
	 * @param <I> the portable options interface
	 * @param <S> the source options type
	 * @param <T> the target options type
	 * @return the converted options, or {@code null}
	 */
	@SuppressWarnings("unchecked")
	public static <I, S extends I, T extends S> @Nullable T copyToTarget(@Nullable S sourceBean,
			Class<I> sourceInterfaceClazz, Class<T> targetBeanClazz) {
		Assert.notNull(sourceInterfaceClazz, "SourceOptionsClazz must not be null");
		Assert.notNull(targetBeanClazz, "TargetOptionsClazz must not be null");
		if (sourceBean == null) {
			return null;
		}
		if (sourceBean.getClass().isAssignableFrom(targetBeanClazz)) {
			return (T) sourceBean;
		}
		try {
			T targetOptions = targetBeanClazz.getConstructor().newInstance();
			return mergeBeans(sourceBean, targetOptions, sourceInterfaceClazz, true);
		}
		catch (Exception ex) {
			throw new RuntimeException(
					"Failed to convert the " + sourceInterfaceClazz.getName() + " into " + targetBeanClazz.getName(),
					ex);
		}
	}

	/**
	 * Merge JavaBean properties declared by a portable options interface.
	 * @param source the source bean
	 * @param target the target bean
	 * @param sourceInterfaceClazz the portable options interface
	 * @param overrideNonNullTargetValues whether source values replace target values
	 * @param <I> the portable options interface
	 * @param <S> the source bean type
	 * @param <T> the target bean type
	 * @return the merged target
	 */
	public static <I, S extends I, T extends S> T mergeBeans(S source, T target, Class<I> sourceInterfaceClazz,
			boolean overrideNonNullTargetValues) {
		Assert.notNull(source, "Source object must not be null");
		Assert.notNull(target, "Target object must not be null");

		BeanWrapper sourceBeanWrapper = new BeanWrapperImpl(source);
		BeanWrapper targetBeanWrapper = new BeanWrapperImpl(target);
		Set<String> interfaceMethodNames = Arrays.stream(sourceInterfaceClazz.getMethods())
			.map(Method::getName)
			.collect(Collectors.toSet());

		for (PropertyDescriptor descriptor : sourceBeanWrapper.getPropertyDescriptors()) {
			String propertyName = descriptor.getName();
			if (BEAN_MERGE_FIELD_EXCLUSIONS.contains(propertyName)
					|| !interfaceMethodNames.contains(toGetterName(propertyName))) {
				continue;
			}
			Object value = sourceBeanWrapper.getPropertyValue(propertyName);
			if (value != null) {
				Object targetValue = targetBeanWrapper.getPropertyValue(propertyName);
				if (targetValue == null || overrideNonNullTargetValues) {
					targetBeanWrapper.setPropertyValue(propertyName, value);
				}
			}
		}
		return target;
	}

	private static String toGetterName(String name) {
		return "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	/**
	 * Generate a JSON schema for a type.
	 * @param inputType the input type
	 * @param toUpperCaseTypeValues whether JSON schema type values should be upper-case
	 * @return the generated JSON schema
	 */
	public static String getJsonSchema(Type inputType, boolean toUpperCaseTypeValues) {
		String schema = JsonSchemaUtils.getJsonSchema(inputType).toPrettyString();
		if (!toUpperCaseTypeValues) {
			return schema;
		}
		try {
			ObjectNode node = (ObjectNode) Objects.requireNonNull(OBJECT_MAPPER.readTree(schema));
			toUpperCaseTypeValues(node);
			return node.toPrettyString();
		}
		catch (JacksonException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Convert all JSON schema {@code type} values to upper-case.
	 * @param node the schema node to update
	 */
	public static void toUpperCaseTypeValues(ObjectNode node) {
		uppercaseTypeValues(node);
	}

	private static void uppercaseTypeValues(@Nullable JsonNode node) {
		if (node == null) {
			return;
		}
		if (node.isObject()) {
			ObjectNode objectNode = (ObjectNode) node;
			objectNode.properties().forEach(entry -> {
				JsonNode value = entry.getValue();
				if ("type".equals(entry.getKey()) && value.isString()) {
					objectNode.put("type", value.asString().toUpperCase());
				}
				else {
					uppercaseTypeValues(value);
				}
			});
		}
		else if (node.isArray()) {
			node.forEach(ModelOptionsUtils::uppercaseTypeValues);
		}
	}

	/**
	 * Return the runtime value if not empty, or else the default value.
	 */
	@Contract("_, !null -> !null")
	public static <T> @Nullable T mergeOption(@Nullable T runtimeValue, @Nullable T defaultValue) {
		return ObjectUtils.isEmpty(runtimeValue) ? defaultValue : runtimeValue;
	}

	/**
	 * JSON property inspection result.
	 *
	 * @param acceptAllFields whether arbitrary properties are accepted
	 * @param properties explicitly accepted properties
	 */
	public record JsonPropertyResult(boolean acceptAllFields, List<String> properties) {
	}

}

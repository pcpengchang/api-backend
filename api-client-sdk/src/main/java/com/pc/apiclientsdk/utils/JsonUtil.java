package com.pc.apiclientsdk.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import com.pc.apiclientsdk.http.HttpDataSourceRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
public class JsonUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final ObjectMapper ALLOW_NULL_OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ALLOW_NULL_OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.ALWAYS);
    }

    public static JsonNode toJsonNode(Object src) {
        return OBJECT_MAPPER.valueToTree(src);
    }

    public static JsonNode strToJsonNode(String src) {
        if (StringUtils.isBlank(src)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readTree(src);
        } catch (JsonProcessingException e) {
            log.error("JsonUtil.strToJsonNode error", e);
            return null;
        }
    }

    public static String toJson(Object src) {
        try {
            return OBJECT_MAPPER.writeValueAsString(src);
        } catch (JsonProcessingException e) {
            log.error("JsonUtil.toJson error", e);
            return null;
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            log.error("JsonUtil.fromJson error", e);
            return null;
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            log.error("JsonUtil.fromJson error", e);
            return null;
        }
    }

    public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
        return OBJECT_MAPPER.convertValue(fromValue, toValueType);
    }

    public static JsonNode combineJsonNode(JsonNode mainJsonNode, JsonNode updateJsonNode) {
        if (mainJsonNode == null && updateJsonNode == null) {
            return null;
        }
        if (mainJsonNode == null) {
            return updateJsonNode;
        }
        if (updateJsonNode == null) {
            return mainJsonNode;
        }
        JsonNode mergeNode = mainJsonNode.deepCopy();
        merge(mergeNode, updateJsonNode);
        return mergeNode;
    }

    public static Map<String, Object> toMap(String jsonStr) {
        try {
            return ALLOW_NULL_OBJECT_MAPPER.readValue(jsonStr, Map.class);
        } catch (IOException e) {
            log.error("JsonUtil.toMap error", e);
            return Collections.emptyMap();
        }

    }

    private static void merge(JsonNode mainNode, JsonNode updateNode) {
        Iterator<String> fieldNames = updateNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode jsonNode = mainNode.get(fieldName);
            // if field exists and is an embedded object
            if (jsonNode != null && jsonNode.isObject()) {
                merge(jsonNode, updateNode.get(fieldName));
            } else if (mainNode instanceof ObjectNode) {
                // Overwrite field
                JsonNode value = updateNode.get(fieldName);
                ((ObjectNode) mainNode).set(fieldName, value);
            }
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String pageParam = "{\"pageParam\":\"123\"}";

        Map<String, String> pageHeaderParams = Maps.newHashMap();
        pageHeaderParams.put("name", "测试");
        HttpDataSourceRequest httpDataSourceRequest = new HttpDataSourceRequest();
        httpDataSourceRequest.setHttpHeaders(pageHeaderParams);
        httpDataSourceRequest.setRemoteIp("http://120.25.220.64");

        JsonNode commonParam = JsonUtil.combineJsonNode(strToJsonNode(pageParam), JsonUtil.toJsonNode(httpDataSourceRequest));
        System.out.println(commonParam);

        String bodyParam = "{\n  \"code\": 1,\n  \"username\": \"pengchang\"\n}";
        ((ObjectNode) commonParam).set("httpBody", strToJsonNode(bodyParam));
        System.out.println(commonParam);
    }
}

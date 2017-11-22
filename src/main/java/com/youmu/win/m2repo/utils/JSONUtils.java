package com.youmu.win.m2repo.utils;

import java.io.IOException;
import java.text.DateFormat;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.TokenBuffer;

/**
 * Created by dehua.lai on 2017/7/11.
 */
public class JSONUtils {

    private static ObjectMapper objectMapper = new ObjectMapper()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    public static <T> String serialize(T obj) throws JsonProcessingException {
        if (null == obj) {
            return null;
        }
        return objectMapper.writeValueAsString(obj);
    }

    /*
     * String jsonString="[{'id':'1'},{'id':'2'}]"; ObjectMapper mapper = new
     * ObjectMapper(); JavaType javaType =
     * mapper.getTypeFactory().constructParametricType(List.class, Bean.class);
     * //如果是Map类型
     * mapper.getTypeFactory().constructParametricType(HashMap.class,String.
     * class, Bean.class); List<Bean> lst =
     * (List<Bean>)mapper.readValue(jsonString, javaType);
     */
    public static <T> T deserialize(String str, Class<T> clazz) throws IOException {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        return objectMapper.readValue(str, clazz);
    }

    /**
     * @param str
     * @param tf
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T deserialize(String str, TypeReference<T> tf) throws IOException {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        return objectMapper.readValue(str, tf);
    }

    /**
     * 此方法创建一个JsonNode是对象类型的，如果是array类型则使用 @cn.ucmed.blmaster.utils.JSONUtils#createArray()
     * JsonNode ↑ | | ArrayNode ObjectNode
     * @return
     */
    public static ObjectNode createNode() {
        return objectMapper.createObjectNode();
    }

    public static ArrayNode createArray() {
        return objectMapper.createArrayNode();
    }

    /**
     * 所有 obj里的date类型都会按照df转
     * @param obj
     * @param df
     * @param <T>
     * @return
     */
    public static <T> String serialize(T obj, DateFormat df) throws JsonProcessingException {
        return objectMapper.writer().with(df).writeValueAsString(obj);
    }

    public static <T> JsonNode serializeJsonNode(T obj, DateFormat df) throws IOException {
        if (obj == null)
            return null;
        TokenBuffer buf = null;
        try {
            buf = new TokenBuffer(objectMapper, false);
            if (objectMapper.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                buf = buf.forceUseOfBigDecimal(true);
            }
            JsonNode result;
            objectMapper.writer().with(df).writeValue(buf, obj);
            JsonParser p = buf.asParser();
            result = objectMapper.readTree(p);
            p.close();
            return result;
        } finally {
            if (null != buf) {
                buf.close();
            }
        }
    }
}

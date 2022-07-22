/*
 * Copyright: Copyright (c)2011
 * Company: 易宝支付(YeePay)
 */

package com.cb.gulimall.product.suppot;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.TimeZone;

/**
 * title: <br>
 * description: 描述<br>
 * Copyright: Copyright (c)2014<br>
 * Company: 易宝支付(YeePay)<br>
 *
 * @author dreambt
 * @version 1.0.0
 * @since 2020/11/11 上午10:59
 */
public class JsonMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonMapper.class);

    protected ObjectMapper mapper;

    public JsonMapper() {
        this((Include) null);
    }

    public JsonMapper(Include include) {
        this.mapper = new ObjectMapper();
        if (include != null) {
            this.mapper.setSerializationInclusion(include);
        }

        this.mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        this.mapper.setTimeZone(TimeZone.getDefault());
    }

    public static Map toMap(String jsonStr) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return (Map) objectMapper.readValue(jsonStr, Map.class);
        } catch (Exception var3) {
            LOGGER.error("转换JSON字符串失败：{}", jsonStr, var3);
            return null;
        }
    }

    public static JsonMapper nonEmptyMapper() {
        return new JsonMapper(Include.NON_EMPTY);
    }

    public static JsonMapper nonDefaultMapper() {
        return new JsonMapper(Include.NON_DEFAULT);
    }

    public static JsonMapper defaultMapper() {
        return new JsonMapper();
    }

    public static JsonMapper nonNullMapper() {
        return new JsonMapper(Include.NON_NULL);
    }

    public static ObjectMapper nonEmptyObjectMapper() {
        return nonEmptyMapper().getMapper();
    }

    public static ObjectMapper nonDefaultObjectMapper() {
        return nonDefaultMapper().getMapper();
    }

    public static ObjectMapper nonNullObjectMapper() {
        return nonNullMapper().getMapper();
    }

    public String toJson(Object object) {
        try {
            return this.mapper.writeValueAsString(object);
        } catch (IOException var3) {
            LOGGER.warn("write to json string error:" + object, var3);
            return null;
        }
    }

    public <T> T fromJson(String jsonString, Class<T> clazz) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        } else {
            try {
                return this.mapper.readValue(jsonString, clazz);
            } catch (IOException var4) {
                LOGGER.warn("parse json string error:" + jsonString, var4);
                return null;
            }
        }
    }

    public <T> T fromJson(String jsonString, JavaType javaType) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        } else {
            try {
                return this.mapper.readValue(jsonString, javaType);
            } catch (IOException var4) {
                LOGGER.warn("parse json string error:" + jsonString, var4);
                return null;
            }
        }
    }

    public JavaType contructCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
        return this.mapper.getTypeFactory().constructCollectionType(collectionClass, elementClass);
    }

    public JavaType contructMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
        return this.mapper.getTypeFactory().constructMapType(mapClass, keyClass, valueClass);
    }

    public void update(String jsonString, Object object) {
        try {
            this.mapper.readerForUpdating(object).readValue(jsonString);
        } catch (IOException var4) {
            LOGGER.warn("update json string:" + jsonString + " to object:" + object + " error.", var4);
        }

    }

    public String toJsonP(String functionName, Object object) {
        return this.toJson(new JSONPObject(functionName, object));
    }

    public void enableEnumUseToString() {
        this.mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        this.mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    }

    public ObjectMapper getMapper() {
        return this.mapper;
    }

}

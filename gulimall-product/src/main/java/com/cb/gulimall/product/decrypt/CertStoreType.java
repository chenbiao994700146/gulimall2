package com.cb.gulimall.product.decrypt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.Maps;

import java.util.Map;

public enum CertStoreType {

    STRING("string", "明文"),
    FILE_P12("file_p12", "文件");

    private static final Map<String, CertStoreType> VALUE_MAP = Maps.newHashMap();

    private final String value;
    private final String displayName;

    static {
        for (CertStoreType item : CertStoreType.values()) {
            VALUE_MAP.put(item.value, item);
        }
    }

    CertStoreType(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    @JsonCreator
    public static CertStoreType parse(String value) {
        return VALUE_MAP.get(value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Map<String, CertStoreType> getValueMap() {
        return VALUE_MAP;
    }
}

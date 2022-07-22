package com.cb.gulimall.product.config;

import com.cb.gulimall.product.decrypt.CertStoreType;
import com.cb.gulimall.product.dto.CertTypeEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public final class YopCertConfig implements Serializable {

    private static final long serialVersionUID = -6377916283927611130L;

    @JsonProperty("store_type")
    private CertStoreType storeType;

    @JsonProperty("cert_type")
    private CertTypeEnum certType;

    private String password;

    private String value;

    public CertStoreType getStoreType() {
        return storeType;
    }

    public void setStoreType(CertStoreType storeType) {
        this.storeType = storeType;
    }

    public CertTypeEnum getCertType() {
        return certType;
    }

    public void setCertType(CertTypeEnum certType) {
        this.certType = certType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
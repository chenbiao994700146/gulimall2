package com.cb.gulimall.product.decrypt;

/**
 * title: 易宝常量<br>
 * description: 描述<br>
 * Copyright: Copyright (c)2014<br>
 * Company: 易宝支付(YeePay)<br>
 *
 * @author wdc
 * @version 1.0.0
 * @since 2019-06-13 15:46
 */
public final class YopConstant {

    /**
     * 易宝默认公钥
     */
    public static final String YOP_RSA_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4g7dPL+CBeuzFmARI2GFjZpKODUROaMG+E6wdNfv5lhPqC3jjTIeljWU8AiruZLGRhl92QWcTjb3XonjaV6k9rf9adQtyv2FLS7bl2Vz2WgjJ0FJ5/qMaoXaT+oAgWFk2GypyvoIZsscsGpUStm6BxpWZpbPrGJR0N95un/130cQI9VCmfvgkkCaXt7TU1BbiYzkc8MDpLScGm/GUCB2wB5PclvOxvf5BR/zNVYywTEFmw2Jo0hIPPSWB5Yyf2mx950Fx8da56co/FxLdMwkDOO51Qg3fbaExQDVzTm8Odi++wVJEP1y34tlmpwFUVbAKIEbyyELmi/2S6GG0j9vNwIDAQAB";

    /**
     * yop请求头：签名
     */
    public static final String HEADER_SIGN = "x-yop-sign";

    /**
     * yop请求头：签名类型
     */
    public static final String HEADER_SIGN_TYPE = "x-yop-sign-type";

    /**
     * yop请求头：appid
     */
    public static final String HEADER_APP_ID = "x-yop-appid";

    /**
     * yop请求头：请求id（便于定位问题）
     */
    public static final String HEADER_REQUEST_ID = "x-yop-request-id";

    /**
     * yop请求头：事件类型
     */
    public static final String HEADER_EVENT_TYPE = "x-yop-event-type";

    /**
     * yop请求头：报文体 SHA256
     */
    public static final String HEADER_CONTENT_SHA256 = "x-yop-content-sha256";

    /**
     * yop必传请求头
     */
    public static final String[] STANDARD_HEADERS = {HEADER_SIGN, HEADER_SIGN_TYPE, HEADER_EVENT_TYPE};

    /**
     * yop请求头前缀
     */
    public static final String STANDAED_HEADER_PREFIX = "x-yop-";
}

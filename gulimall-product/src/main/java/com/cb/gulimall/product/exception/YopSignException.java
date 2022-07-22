package com.cb.gulimall.product.exception;

/**
 * title: 签名异常<br>
 * description: 描述<br>
 * Copyright: Copyright (c)2014<br>
 * Company: 易宝支付(YeePay)<br>
 *
 * @author wdc
 * @version 1.0.0
 * @since 2019-06-13 19:16
 */
public class YopSignException extends YopBizException {

    private static final String CODE = "yop.sign.error";

    public YopSignException() {
    }

    public YopSignException(String message) {
        super(message);
    }

    public YopSignException(String message, Object... args) {
        super(message, args);
    }

    @Override
    public String getCode() {
        return CODE;
    }
}

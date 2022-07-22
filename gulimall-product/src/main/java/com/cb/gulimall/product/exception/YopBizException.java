package com.cb.gulimall.product.exception;

import java.text.MessageFormat;

/**
 * title: 业务异常(校验)<br>
 * description: 描述<br>
 * Copyright: Copyright (c)2014<br>
 * Company: 易宝支付(YeePay)<br>
 *
 * @author wdc
 * @version 1.0.0
 * @since 2019-06-13 17:11
 */
public abstract class YopBizException extends RuntimeException {

    public YopBizException() {
    }

    public YopBizException(String message) {
        super(message, null, false, false);
    }

    public YopBizException(String message, Object... args) {
        super(message != null && args != null ? MessageFormat.format(message, args) : message, null, false, false);
    }

    public abstract String getCode();
}

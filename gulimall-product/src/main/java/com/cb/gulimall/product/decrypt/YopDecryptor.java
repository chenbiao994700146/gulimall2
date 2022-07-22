/*
 * Copyright: Copyright (c)2011
 * Company: 易宝支付(YeePay)
 */
package com.cb.gulimall.product.decrypt;

import com.cb.gulimall.product.cert.CertLocator;
import com.cb.gulimall.product.dto.DecryptParamDTO;

/**
 * title: <br>
 * description: 描述<br>
 * Copyright: Copyright (c)2014<br>
 * Company: 易宝支付(YeePay)<br>
 *
 * @author wdc
 * @version 1.0.0
 * @since 2021-02-25
 */
public interface YopDecryptor {

    String decrypt(DecryptParamDTO decryptParamDTO, CertLocator certLocator);

}

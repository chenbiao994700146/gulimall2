/*
 * Copyright: Copyright (c)2011
 * Company: 易宝支付(YeePay)
 */
package com.cb.gulimall.product.decrypt;

import com.cb.gulimall.product.dto.CertTypeEnum;


import java.util.LinkedHashMap;
import java.util.Map;

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
public class YopDecryptorFactory {

    private static Map<CertTypeEnum, YopDecryptor> decryptorMap = new LinkedHashMap<>();

    static {
        decryptorMap.put(CertTypeEnum.RSA2048, new YopRsaDecryptor());
        decryptorMap.put(CertTypeEnum.SM4, new YopSm4Decryptor());
    }

    public static YopDecryptor getDecryptor(CertTypeEnum certType) {
        return decryptorMap.get(certType);
    }
}

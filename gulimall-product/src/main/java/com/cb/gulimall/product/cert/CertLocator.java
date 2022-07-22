/*
 * Copyright: Copyright (c)2011
 * Company: 易宝支付(YeePay)
 */

package com.cb.gulimall.product.cert;

import com.cb.gulimall.product.config.YopCertConfig;


import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

/**
 * title: 证书定位器<br>
 * description: 描述<br>
 * Copyright: Copyright (c)2014<br>
 * Company: 易宝支付(YeePay)<br>
 *
 * @author dreambt
 * @version 1.0.0
 * @since 2020/11/9 下午12:13
 */
public interface CertLocator {

    PublicKey getYopPublicKey();

    List<PrivateKey> getIsvPrivateKey(String appId);

    List<YopCertConfig> getIsvEncryptKeys(String appId);

}

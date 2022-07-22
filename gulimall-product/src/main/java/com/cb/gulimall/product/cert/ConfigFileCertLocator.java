/*
 * Copyright: Copyright (c)2011
 * Company: 易宝支付(YeePay)
 */

package com.cb.gulimall.product.cert;


import com.cb.gulimall.product.config.YopCertConfig;
import com.cb.gulimall.product.decrypt.RSA;
import com.cb.gulimall.product.decrypt.YopConstant;
import org.apache.commons.lang3.StringUtils;


import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * title: 基于配置文件的证书定位器<br>
 * description: 支持从配置文件加载，适合单appId商户使用<br>
 * Copyright: Copyright (c)2014<br>
 * Company: 易宝支付(YeePay)<br>
 *
 * @author dreambt
 * @version 1.0.0
 * @since 2020/11/9 下午12:14
 */
public class ConfigFileCertLocator implements CertLocator {

    private PublicKey yopPublicKey;
    private Map<String, List<PrivateKey>> isvPrivateKeyMap;
    private List<YopCertConfig> isvEncryptKeys;

    @Override
    public PublicKey getYopPublicKey() {
        return yopPublicKey;
    }

    @Override
    public List<PrivateKey> getIsvPrivateKey(String appId) {
        return isvPrivateKeyMap.get(appId);
    }

    @Override
    public List<YopCertConfig> getIsvEncryptKeys(String appId) {
        return isvEncryptKeys;
    }

    public void setYopPublicKey(String yopPublicKeyString) {
        if (StringUtils.isEmpty(yopPublicKeyString)) {
            yopPublicKeyString = YopConstant.YOP_RSA_PUBLIC_KEY;
        }
        this.yopPublicKey = RSA.string2PublicKey(yopPublicKeyString);
        this.yopPublicKey = yopPublicKey;
    }

    public void setIsvPrivateKeyMap(Map<String, List<String>> isvPrivateKeyStringMap) {
        isvPrivateKeyMap = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : isvPrivateKeyStringMap.entrySet()) {
            String appId = entry.getKey();
            List<PrivateKey> privateKeyList = new ArrayList<>();
            for (String isvPrivateKeyString : entry.getValue()) {
                privateKeyList.add(RSA.string2PrivateKey(isvPrivateKeyString));
            }
            isvPrivateKeyMap.put(appId, privateKeyList);
        }
        this.isvPrivateKeyMap = isvPrivateKeyMap;
    }

    public void setIsvEncryptKeys(List<YopCertConfig> isvEncryptKeys) {
        this.isvEncryptKeys = isvEncryptKeys;
    }
}

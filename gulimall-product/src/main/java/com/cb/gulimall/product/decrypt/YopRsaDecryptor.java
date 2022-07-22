/*
 * Copyright: Copyright (c)2011
 * Company: 易宝支付(YeePay)
 */
package com.cb.gulimall.product.decrypt;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.cb.gulimall.product.cert.CertLocator;
import com.cb.gulimall.product.dto.DecryptParamDTO;
import com.cb.gulimall.product.exception.YopSignException;
import com.cb.gulimall.product.suppot.Cryptos;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

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
public class YopRsaDecryptor implements YopDecryptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(YopRsaDecryptor.class);

    private CertLocator certLocator;

    @Override
    public String decrypt(DecryptParamDTO decryptParamDTO, CertLocator certLocator) {
        List<PrivateKey> isvPrivateKeys = certLocator.getIsvPrivateKey(decryptParamDTO.getAppId());
        if (CollectionUtils.isNotEmpty(isvPrivateKeys)) {
            for (PrivateKey isvPrivateKey : isvPrivateKeys) {
                try {
                    return decrypt(decryptParamDTO.getCipher(), isvPrivateKey, certLocator.getYopPublicKey());
                } catch (Exception e) {
                    LOGGER.warn("decrypt msg fail,", e);
                }
            }
        }
        throw new YopSignException("fail to decrypt by rsa.");
    }

    public void setCertLocator(CertLocator certLocator) {
        this.certLocator = certLocator;
    }

    private String decrypt(String cipherText, PrivateKey privateKey, PublicKey yopPublicKey) {
        String[] args = cipherText.split("\\$");
        if (args.length != 4) {
            throw new RuntimeException("rsa source invalid : " + cipherText);
        } else {
            String encryptedRandomKeyToBase64 = args[0];
            String encryptedDataToBase64 = args[1];

            byte[] randomKey = RSA.decrypt(Encodes.decodeBase64(encryptedRandomKeyToBase64), privateKey);
            String data = Cryptos.aesDecrypt(Encodes.decodeBase64(encryptedDataToBase64), randomKey);
            String sourceData = StringUtils.substringBeforeLast(data, "$");
            String signToBase64 = StringUtils.substringAfterLast(data, "$");
            boolean verifySign = RSA.verifySign(sourceData, signToBase64, yopPublicKey);
            if (!verifySign) {
                throw new YopSignException("verifySign fail!");
            } else {
                return sourceData;
            }
        }
    }
}

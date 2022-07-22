/*
 * Copyright: Copyright (c)2011
 * Company: 易宝支付(YeePay)
 */
package com.cb.gulimall.product.decrypt;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.cb.gulimall.product.cert.CertLocator;
import com.cb.gulimall.product.config.YopCertConfig;
import com.cb.gulimall.product.dto.CertTypeEnum;
import com.cb.gulimall.product.dto.DecryptParamDTO;
import com.cb.gulimall.product.exception.YopSignException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class YopSm4Decryptor implements YopDecryptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(YopSm4Decryptor.class);

    @Override
    public String decrypt(DecryptParamDTO decryptParamDTO, CertLocator certLocator) {
        List<YopCertConfig> isvEncryptKeys = certLocator.getIsvEncryptKeys(decryptParamDTO.getAppId());
        if (CollectionUtils.isNotEmpty(isvEncryptKeys)) {
            for (YopCertConfig isvEncryptKey : isvEncryptKeys) {
                if (isvEncryptKey.getCertType() == CertTypeEnum.SM4) {
                    final String certKeyBase64 = isvEncryptKey.getValue();
                    try {
                        String once = StringUtils.defaultIfEmpty(decryptParamDTO.getOnce(), null);
                        String associatedData = StringUtils.defaultIfEmpty(decryptParamDTO.getAssociatedData(), null);
                        return new String(Sm4Utils.decrypt_GCM_NoPadding(Encodes.decodeBase64(certKeyBase64),
                                associatedData, once, decryptParamDTO.getCipher()));
                    } catch (Exception e) {
                        LOGGER.warn("fail to try decrypt with sm4 key:" + certKeyBase64 + ", cipherText:" + decryptParamDTO.getCipher() + ", ex:", e);
                    }
                }
            }
        } else {
            throw new YopSignException("no available isv encrypt keys.");
        }
        throw new YopSignException("fail to decrypt by sm4.");
    }
}

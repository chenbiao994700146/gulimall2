package com.cb.gulimall.product.decrypt;

import com.google.common.base.Charsets;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public final class RSA {

    private static final String RSA = "RSA";
    private static final String RSA_ECB_PKCS1PADDING = "RSA/ECB/PKCS1Padding";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    /**
     * string 转 java.security.PublicKey
     *
     * @param pubKey pubKey
     * @return PublicKey
     * @throws InvalidKeySpecException
     */
    public static PublicKey string2PublicKey(String pubKey) {
        try {
            return KeyFactory.getInstance("RSA").generatePublic(
                    new X509EncodedKeySpec(Encodes.decodeBase64(pubKey)));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * string 转java.security.PrivateKey
     *
     * @param priKey 私钥字符串
     * @return 私钥
     * @throws InvalidKeySpecException
     */
    public static PrivateKey string2PrivateKey(String priKey) {

        try {
            return KeyFactory.getInstance(RSA).generatePrivate(
                    new PKCS8EncodedKeySpec(Encodes.decodeBase64(priKey)));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 验证签名
     *
     * @param data      数据
     * @param sign      签名
     * @param publicKey 公钥
     * @return boolean
     */
    public static boolean verifySign(byte[] data, byte[] sign, PublicKey publicKey) {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(data);
            return signature.verify(sign);
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 验证签名
     *
     * @param data     数据
     * @param sign     签名
     * @param pubicKey 公钥
     * @return boolean
     */
    public static boolean verifySign(String data, String sign, PublicKey pubicKey) {
        byte[] dataByte = data.getBytes(Charsets.UTF_8);
        byte[] signByte = Encodes.decodeBase64(sign);
        return verifySign(dataByte, signByte, pubicKey);
    }

    /**
     * 签名
     *
     * @param data 数据
     * @param key  密钥
     * @return byte[]
     */
    public static byte[] sign(byte[] data, PrivateKey key) {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(key);
            signature.update(data);
            return signature.sign();
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 签名
     *
     * @param data 数据
     * @param key  密钥
     * @return String
     */
    public static String sign(String data, PrivateKey key) {
        byte[] dataByte = data.getBytes(Charsets.UTF_8);
        return Encodes.encodeUrlSafeBase64(sign(dataByte, key));
    }

    /**
     * 加密
     *
     * @param data 数据
     * @param key  密钥
     * @return byte[]
     */
    public static byte[] encrypt(byte[] data, Key key) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(RSA_ECB_PKCS1PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 加密
     *
     * @param data 数据
     * @param key  密钥
     * @return String
     */
    public static String encryptToBase64(String data, Key key) {
        try {
            return Encodes.encodeUrlSafeBase64(encrypt(data.getBytes(Charsets.UTF_8), key));
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 解密
     *
     * @param data 数据
     * @param key  密钥
     * @return byte[]
     */
    public static byte[] decrypt(byte[] data, Key key) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1PADDING);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 解密
     *
     * @param data 数据
     * @param key  密钥
     * @return String
     */
    public static String decryptFromBase64(String data, Key key) {
        try {
            return new String(decrypt(Encodes.decodeBase64(data), key), Charsets.UTF_8);
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }
    }

}
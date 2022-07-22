package com.cb.gulimall.product.app;

import com.cb.gulimall.product.cert.CertLocator;
import com.cb.gulimall.product.decrypt.YopDecryptorFactory;
import com.cb.gulimall.product.dto.CertTypeEnum;
import com.cb.gulimall.product.dto.DecryptParamDTO;
import com.cb.gulimall.product.exception.YopSignException;
import com.cb.gulimall.product.suppot.JsonMapper;
import com.fasterxml.jackson.databind.JavaType;
import com.yeepay.yop.sdk.utils.Encodes;
import com.yeepay.yop.sdk.utils.Sm4Utils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

@RestController
public class YiBaoController {
    private JsonMapper jsonMapper;

    private JavaType mapType;

    private static final String SM4_GCM_ALG = "AEAD_SM4_GCM";

    private CertLocator certLocator;

    @PostMapping("/vi/Pay")
    public String YibaoImpl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("进入接受通知");
//         System.out.println(response);
//         String json = IOUtils.toString(request.getReader());
//         String algorithm = request.getParameter("algorithm");
//         System.out.println("algorithm"+algorithm);
//         Map<String, String> params = jsonMapper.fromJson(json, mapType);
//         System.out.println(params);
//        System.out.println(request.getSession());
//        Map<String, String[]> map = request.getParameterMap();
        System.out.println("易宝通知到位了。。。数据：");
        return "SUCCESS";
    }


    /**
     * 清算通知
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @PostMapping("/vi/cs")
    public String YibaoCsImpl(HttpServletRequest request, HttpServletResponse response) throws IOException, GeneralSecurityException {
        System.out.println("进入清算通知");

        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (!(null == ip || "".equals(ip.trim()) || "null".equalsIgnoreCase(ip.trim())) && ip.contains(",")) {
            ip = ip.split(",")[0];
        }

        /**
         * SDK
         */
        String key = "OVQ0Y3JSU2gzZUd4c2hNQw==";
        //解密验签。
        byte[] bytes = Sm4Utils.decrypt_GCM_NoPadding(Encodes.decodeBase64(key), request.getParameter("associatedData"), request.getParameter("once"), request.getParameter("cipherText"));


        System.out.println("bytes+" + new String(bytes));


        System.out.println("易宝通知到位了。。。数据：");
        return "SUCCESS";
    }

    @PostMapping("/vi/Order")
    public String YibaoOrderImpl(HttpServletRequest request, HttpServletResponse response) throws IOException, GeneralSecurityException {


        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (!(null == ip || "".equals(ip.trim()) || "null".equalsIgnoreCase(ip.trim())) && ip.contains(",")) {
            ip = ip.split(",")[0];
        }
        System.out.println("IP=" + ip);
        System.out.println("进入订单通知");
        System.out.println(response);
        System.out.println(request);
        System.out.println(request.getContentType());
        String algorithm = request.getParameter("algorithm");
        System.out.println("algorithm" + algorithm);


        DecryptParamDTO decryptParamDTO = new DecryptParamDTO();
        decryptParamDTO.setAppId(request.getParameter("customerIdentification"));
        if (StringUtils.isNotEmpty(algorithm) && SM4_GCM_ALG.equals(algorithm)) {

            decryptParamDTO.setCertType(CertTypeEnum.SM4);
            decryptParamDTO.setCipher(request.getParameter("cipherText"));
            decryptParamDTO.setOnce(request.getParameter("once"));
            decryptParamDTO.setAssociatedData(request.getParameter("associatedData"));
        } else {
            decryptParamDTO.setCipher(request.getParameter("response"));
        }


        System.out.println("decryptParamDTO" + decryptParamDTO);


        /**
         * SDK
         */
        String key = "OVQ0Y3JSU2gzZUd4c2hNQw==";
        //解密验签。
        byte[] bytes = Sm4Utils.decrypt_GCM_NoPadding(Encodes.decodeBase64(key), request.getParameter("associatedData"), request.getParameter("once"), request.getParameter("cipherText"));


        System.out.println("bytes+" + new String(bytes));


        System.out.println("易宝通知到位了。。。数据：");
        return "SUCCESS";
    }

    private CertTypeEnum resolveCertType(String content) {
        String[] args = content.split("\\$");
        if (args.length == 4 && content.endsWith("$AES$SHA256")) {
            return CertTypeEnum.RSA2048;
        } else {
            return null;
        }
    }
}

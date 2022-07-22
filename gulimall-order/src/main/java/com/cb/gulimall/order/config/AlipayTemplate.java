package com.cb.gulimall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;

import com.cb.gulimall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 *
 */
@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private String app_id = "2021000118673486";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCNhFTMDWaFfsTBrf0Fn5/S+rnNZ7p7Ae3Mt+jYT4u8W6W0y3B1iRrxDMQv+xzxi1S2LFVXr0D4XlPRcO9kR1loQGkD6Pi+cJC7nizSTebLWULwpab52iJ0999IPwg5H3HoNcKP06fIP/bi7xYJ7Jem1spcJSAS/m9UpYoHavKaL5YBJhK0f+i0ROSCP8qhKDvoB4p5R2LL5ZeeuMVdGModAqYe3pi2B0WS01nYU0/BYU3YBEvX+pwiWey5Co+LHRwreEibJcB7pP6gLOtn0W6dCn5gcKUrjbykVbljMa69l/9XybVkSX0H5VSj/Hbf01AHHTMMXBIYtqKQUW744ZxNAgMBAAECggEAf/4x26Q7F35CYpIEiHOIz/8o0ViB1wM1mrSzigDX93JEezKM8iEhFudfAa5yzRwhb+rE/VtqvbhCwukL+3x0ps+qq3iokUWqAabcDxGaLkz7OD/6t3vNMlGFxwgLXe7MzSLiZ8cctwbzOujW2/76YlFeEN0bF1K8Sqi2IGNgLIaIONqQmyP/MK7E/C+4HPnvAoc2/1At2PwYXVPJmJ2/DxeDccW3kSwQSxNArDf8bM0H1AhCNO04tQqunw57a8HKQnnQOhsLDVavTf53W26zMaN6Pi131pXdQp4TDlfoHsYaqtHerCislCTODkBd7IOrgnVjHWm9Uiiyx4zcl0klWQKBgQDNFMEno4aU5uzSNXW9wAINo6UhYpM1b96wbAto+J6sE++8ITgtKjoZn9ej3oAYowWTdVk0iA/cpihQ3B5wqmS9oVAEIPqQ1l1O7s1tFUwc3V1sla9AS1MFhUT4iSRHdleX80C9M5r3atoC/0nhEJcuQBjyqHusrVJUDehvPcrrKwKBgQCwp1k1J4iM1b19bCZ3I6tF1nvrjXF6tNdzFygF5mJvCZ/Nv4WJ5UqLM9Rd1DIbo1/72xIqXuxkCPnjNEFA0tNGsH3kN6SjUD3T5lmWhHS5KxOBnEj5Q+uKm8F1kjLUSPpp7aH4OX7Ck2yfmqH3GATo+W6PNQM0Gz/v8O7ZuT/6ZwKBgQCgkU9qKa6FhwiEzTaSinMRGr9/lB6OCDjjCLUmSwJw2GsrcRb6xcUXqv67XsoMgBctjQN7DczdhXqrVKzAqaiWkGaF1QxZunz0ACT282XUc71h9xBv1on6GI4XyciN6ypJ3jsh5/W9IfvvvNGrVcIIhEcENsYePNXo2brS3h6yYQKBgAVDp2vnFrGjuPbg3zCTtonpP14go+4VL4p1eTLbz7khX0rBV1hppdQKIlz9ctTSBI071QnANGkyv0efCgbFgZhMsCO56MUZIg6LgcXzgor2Fqm5H4+WD5Hc3GIFjlA3MIcxlq8P/5AK1qTW9daoN5U9waaw3LyTx07+Lg/87kQnAoGATNQlKOHn3DPNhtwnpjBhw/xs4cxs5EyqswC5LLiMMHr664YjkH3jk54jm2VVibSF0xDjTvwZbhNJNI1UDJSmcLXxMSoE5S5EEmX5PSNrWe5u2xOoJl5gFUrTYQqHF0eyVy64S4XLlpdcKmLeyoH+0CdBj6hExKAjLnVIF2+jTJA=";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAr4VBgk87skMe/8sC9YfzEiZYnF2oT+D6EJ26tTCtbBKhSkGcJeohNv3pGyozbUOCJ8JI+QLVXnWCQV7U8clUxZeoTpMHNODXHmbvbnhNtCnCV6z+cd//hVQnvYJreWzM5NNy2Pvgv45mLg4GI/9c+yPz/o9JYVdEJsOx5mLPOzP20PvZqzaK0c9t2nk+Wfz+q/1vaPLajacl1O/p8UUs3e4i8vmcUcNsJ9ex9IvF8L3yHGsze2RlNrAt8LbMjQJ2T2q0ez+s13qj5qPepaLWg83CmH3nnTv6b7aNXSf5tIPULsLoG8cct+zGXSbEM0Yysz5r1DgRFw2RUnHiWnM35QIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    public String notify_url = "http://7xck5xy08k.xuduan.vip/payed/notify";


    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    //  public static String return_url = "http://7xck5xy08k.xuduan.vip/alipay.trade.page.pay-JAVA-UTF-8/return_url.jsp";
    public String return_url = "http://member.gulimall.com/memberOrder.html";
    // 签名方式
    private String sign_type = "RSA2";

    // 字符编码格式
    private String charset = "utf-8";

    private String timeout = "30m";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"total_amount\":\"" + total_amount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"timeout_express\":\"" + timeout + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应：" + result);

        return result;

    }
}

package com.cb.gulimall.member;

///import org.junit.jupiter.api.Test;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//@SpringBootTest
public class GulimallMemberApplicationTests {

    @Test
    public void contextLoads() {

        //e10adc3949ba59abbe56e057f20f883e
//        String s = DigestUtils.md5Hex("123456 ");
//        System.out.println(s);
//
//        //盐值加密；随机值
//        String s2 = Md5Crypt.md5Crypt("123456".getBytes());
//        System.out.println(s2);
//        String s1 = Md5Crypt.md5Crypt("123456".getBytes(),"$1$prV4Dqj/");
//        System.out.println(s1);
//

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("123456");
        boolean a = passwordEncoder.matches("123456", "$2a$10$2OKazAy2y6YXU1jEBzVcSeeKeI32rSIQiw4X3ln9lFhji7p7lzBsy");
        System.out.println(a);
        System.out.println(encode);
    }
}

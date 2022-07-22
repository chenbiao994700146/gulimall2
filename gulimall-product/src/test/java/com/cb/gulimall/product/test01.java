package com.cb.gulimall.product;

import com.cb.gulimall.product.dao.AttrGroupDao;
import com.cb.gulimall.product.service.CategoryService;
import com.cb.gulimall.product.vo.SkuItemVo;
import com.cb.gulimall.product.vo.SpuItemAttrGroupVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class test01 {

    @Resource
    CategoryService categoryService;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    RedissonClient redissonClient;

    @Resource
    AttrGroupDao attrGroupDao;

    @Test
    public void test() {
        List<SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupDao.getAttrGroupWithAttrsBySpuId(6L, 225L);
        System.out.println(attrGroupWithAttrsBySpuId);
    }


    @Test
    public void testFindPath() {
        Long[] catelogPath = categoryService.findCatelogPath(225L);
        //System.out.println();
        log.info("完整路径：{}", Arrays.asList(catelogPath));
    }

    @Test
    public void testRedis() {
        //hello  world
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        //保存
        ops.set("hello", "world" + UUID.randomUUID().toString());

        //查询
        String hello = ops.get("hello");
        System.out.println("值为sss：" + hello);
    }


    @Test
    public void testRedisson() {
        System.out.println(redissonClient);
    }
}

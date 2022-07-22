package com.cb.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cb.gulimall.product.service.CategoryBrandRelationService;
import com.cb.gulimall.product.vo.Catelog2Vo;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cb.common.utils.PageUtils;
import com.cb.common.utils.Query;

import com.cb.gulimall.product.dao.CategoryDao;
import com.cb.gulimall.product.entity.CategoryEntity;
import com.cb.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import javax.annotation.Resource;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    private Map<String, Object> cache = new HashMap<>();

    @Resource
    CategoryBrandRelationService categoryBrandRelationService;

    @Resource
    CategoryDao categoryDao;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    RedissonClient redissonClient;


//    @Resource
//    private  StringRedisTemplate redisTemplate;

//    @Resource(name="stringRedisTemplate")
//    ValueOperations<String, String> valOpsObj;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1.查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);

        //2.组装成父子的树形结构
        //2.1) 找到所有的一级分类
//        entities.stream().filter((categoryEntity)->{
//            return categoryEntity.getParentCid()==0;
//        }).collect(Collectors.toList());
        //简化写法
        List<CategoryEntity> level1Menus = entities.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == 0
        ).map((menu) -> {
            menu.setChildren(getChildrens(menu, entities));
            return menu;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());


        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO  1.检查当前删除的菜单，是否被别的地方引用

        //逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);

        Collections.reverse(parentPath);

        return parentPath.toArray(new Long[parentPath.size()]);
    }

    /**
     * 级联更新所有关联的数据
     *
     * @CacheEvict :失效模式的使用
     * <p>
     * 1、同时进行多种缓存操作 @Caching
     * 2、指定删除某个分区下的所有数据 @CacheEvict(value = "category",allEntries = true)
     * 3、存储统一类型的数据，都可以指定成同一个分区,分区名默认就是缓存的前缀
     */
    // @CacheEvict(value = "category",key = "'Level1Categorys'")
//    @Caching(evict = {
//            @CacheEvict(value = "category",key = "'Level1Categorys'"),
//            @CacheEvict(value = "category",key = "'getCatalogJson'")
//
//    })
    @CacheEvict(value = "category", allEntries = true)
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    //TODO 产生对外内存溢出：
    //1)\springbo
    //没一个需要缓存的数据我们都来指定要放到那个名字的缓存【缓存的分区（按照业务类型分）】
    @Cacheable(value = {"category"}, key = "'Level1Categorys'", sync = true)
    //代表当前方法的结果需要缓存，如果缓存中有，方法不用调用，如果缓存中没有，就会调用方法，最后将方法的结果放入缓存
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        System.out.println("getLevel1Categorys......");
        long l = System.currentTimeMillis();
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        System.out.println("消耗时间：" + (System.currentTimeMillis() - l));
        return categoryEntities;
        // return null;
    }

    @Cacheable(value = "category", key = "'getCatalogJson'")
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        //1.查出所有1级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);

        //2.封装数据 return k.getCatId().toString();
        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {

            //1.没一个的一级分类，查出他的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    //1.找当前二级分类的三级分类，封装成vo
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());
                    if (level3Catelog != null) {
                        List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
                            //2.封装成指定格式
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());

                            return catelog3Vo;
                        }).collect(Collectors.toList());

                        catelog2Vo.setCatalog3List(collect);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }

            return catelog2Vos;
        }));


        return parent_cid;
    }

    //@Override
    public Map<String, List<Catelog2Vo>> getCatalogJson2() {
        //给缓存中放jsoN字符串，拿出的json字符串还有转为能用的对象类型【序列化和反序列化】


        /**
         * 1.加上空结果缓存：解决缓存穿透
         * 2.设置过期时间（加随机值）：解决缓存雪崩 set("catalogJson",s,1, TimeUnit.DAYS);
         * 3.加上锁：解决缓存击穿
         *
         */
        //1.加入缓存逻辑,缓存中存的数据是JSON字符串
        //JSON跨语言，跨平台
        String catalogJson = redisTemplate.opsForValue().get("catalogJson");
        // String catalogJson =valOpsObj.get("catalogJson");
        // Map<String, List<Catelog2Vo>> catalogJsonFromDb =null;

        if (StringUtils.isEmpty(catalogJson)) {
            //2.缓存中没有
            System.out.println("缓存不命中。。。。查询数据库");
            Map<String, List<Catelog2Vo>> atalogJsonFromDb = getCatalogJsonFromDbWithRedisLock();
            //valOpsObj.set("catalogJson",s);
            return atalogJsonFromDb;

        }
        //4.转为指定的对象
        System.out.println("缓存命中。。。。");
        Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });
        return result;
    }


    /**
     * 缓存里面的数据如何和数据库保持一致
     * 缓存数据一致行
     * 1）、双写模式
     * 2）、失效模式
     *
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedissionLock() {

        //锁的名字，锁的粒度，越细越快
        //锁的粒度：11-商品；product-11
        RLock lock = redissonClient.getLock("catalogJson-lock");
        lock.lock();

        Map<String, List<Catelog2Vo>> dataFromDb;
        try {
            dataFromDb = getDataFromDb();
        } finally {
            lock.unlock();
        }


        return dataFromDb;


    }


    //从数据库查询并封装数据
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedisLock() {

        //1.占分布式锁。去redis占坑
        String uuid = UUID.randomUUID().toString();
        Boolean locak = redisTemplate.opsForValue().setIfAbsent("locak", uuid, 300, TimeUnit.SECONDS);
        if (locak) {
            System.out.println("获取分布式锁成功。。。");
            //加锁成功。。。
            //2.设置过期时间,必须和加锁是同步的，原子的
            // redisTemplate.expire("locak",30,TimeUnit.SECONDS);
            Map<String, List<Catelog2Vo>> dataFromDb;
            try {
                dataFromDb = getDataFromDb();
            } finally {
                String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                //删除锁
                Long locak1 = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class)
                        , Arrays.asList("locak"), uuid);
            }

            // redisTemplate.delete("locak");

            //获取值对比+对比成功删除=原子操作
//        String locaValue = redisTemplate.opsForValue().get("locak");
//        if(uuid.equals(locaValue)){
//            //删除自己的锁
//            redisTemplate.delete("locak");
//        }


            return dataFromDb;
        } else {
            //没成功-...重试
            //休眠100毫秒
            System.out.println("获取分布式锁失败。。。等待重试");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCatalogJsonFromDbWithRedisLock();
        }

    }

    private Map<String, List<Catelog2Vo>> getDataFromDb() {
        String catalogJson = redisTemplate.opsForValue().get("catalogJson");

        if (!StringUtils.isEmpty(catalogJson)) {
            //缓存不为空
            Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
            return result;
        }
        System.out.println("查询了数据库。。。。");

        List<CategoryEntity> selectList = baseMapper.selectList(null);

        //1.查出所有1级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);

        //2.封装数据 return k.getCatId().toString();
        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {

            //1.没一个的一级分类，查出他的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    //1.找当前二级分类的三级分类，封装成vo
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());
                    if (level3Catelog != null) {
                        List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
                            //2.封装成指定格式
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());

                            return catelog3Vo;
                        }).collect(Collectors.toList());

                        catelog2Vo.setCatalog3List(collect);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }

            return catelog2Vos;
        }));

        //3.查到的数据再放入缓存,将对象转为JSON
        String s = JSON.toJSONString(parent_cid);

        redisTemplate.opsForValue().set("catalogJson", s, 1, TimeUnit.DAYS);

        return parent_cid;
    }

    //从数据库查询并封装数据
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithLocalLock() {
        //1.如果缓存中有，就用缓存的
        // Map<String, List<Catelog2Vo>> catalogJson =(Map<String, List<Catelog2Vo>>) cache.get("catalogJson");
//        if(cache.get("catalogJson")==null){
//
//        }
//       return catalogJson;
// cache.put("catalogJson",parent_cid);
        //只要是同一把锁，就能锁住，需要这个锁的所有线程
        //1、synchronized (this)；springboot所有组件在容器中都是单列的

        synchronized (this) {//同步
            //的到锁以后，我们应该再去缓存中确定一次，如果没有才需要继续查询

            return getDataFromDb();
        }


    }


    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid() == parent_cid).collect(Collectors.toList());


        // return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
        return collect;
    }


    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        //1.收集当前节点id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }

    //递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {

        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            //找到子菜单
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            //菜单排序
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return children;
    }

}
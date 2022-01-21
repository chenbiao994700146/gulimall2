package com.cb.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cb.common.utils.R;
import com.cb.gulimall.cart.feign.ProductFeignService;
import com.cb.gulimall.cart.interceptor.CartInterceptor;
import com.cb.gulimall.cart.service.CartService;
import com.cb.gulimall.cart.vo.CartItemVo;
import com.cb.gulimall.cart.vo.CartVo;
import com.cb.gulimall.cart.vo.SkuInfoVo;
import com.cb.gulimall.cart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundGeoOperations;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Resource
    ProductFeignService productFeignService;

    @Resource
    ThreadPoolExecutor executor;
    private final String CART_PREFIX="gulimall:cart";

    @Override
    public CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String,Object,Object> cartOps = getCartOps();

        String res = (String) cartOps.get(skuId.toString());
        if(StringUtils.isEmpty(res)){
            CartItemVo cartItemVo = new CartItemVo();
            CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
                //1、远程查询当前要添加的商品信息
                R skuInfo = productFeignService.info(skuId);
                SkuInfoVo data = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                //2、商品添加到购物车

                cartItemVo.setCheck(true);
                cartItemVo.setCount(num);
                cartItemVo.setImage(data.getSkuDefaultImg());
                cartItemVo.setTitle(data.getSkuTitle());
                cartItemVo.setSkuId(skuId);
                cartItemVo.setPrice(data.getPrice());
            }, executor);


            //3、远程查询sku的销售信息
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                List<String> attrValues = productFeignService.getSkuSaleAttrValues(skuId);
                cartItemVo.setSkuAttrValues(attrValues);
            }, executor);

            CompletableFuture.allOf(future1,future).get();
            String s = JSON.toJSONString(cartItemVo);
            cartOps.put(skuId.toString(),s);
            return cartItemVo;
        }else{
            CartItemVo cartItemVo = new CartItemVo();
            //购物车有此商品，修改数据量
            cartItemVo= JSON.parseObject(res,CartItemVo.class);
            cartItemVo.setCount(cartItemVo.getCount()+num);

            cartOps.put(skuId.toString(),JSON.toJSONString(cartItemVo));
            return cartItemVo;
        }




    }

    @Override
    public CartItemVo getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        String str = (String) cartOps.get(skuId.toString());
        CartItemVo cartItemVo = JSON.parseObject(str, CartItemVo.class);
        return cartItemVo;
    }

    @Override
    public CartVo getCart() throws ExecutionException, InterruptedException {
        CartVo cartVo = new CartVo();

        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if(userInfoTo.getUserId()!=null){
            //1、登录
            String cartKey =CART_PREFIX+userInfoTo.getUserId();
            BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(cartKey);
            //2、如果临时购物车的数据还没有进行合并
            List<CartItemVo> tempCartItems = getCartItems(CART_PREFIX + userInfoTo.getUserKey());
            if(tempCartItems!=null){
                //临时购物车有数据，需要合并
                for (CartItemVo item : tempCartItems) {
                    addToCart(item.getSkuId(),item.getCount());
                }
                //清除临时购物车的数据
                clearCart(CART_PREFIX + userInfoTo.getUserKey());
            }
            //3、获取登录后的购物车的数据【包含合并过来的临时购物车的数据，和登录后的购物车的数据】
            List<CartItemVo> cartItems = getCartItems(cartKey);
            cartVo.setItems(cartItems);

        }else{

            //2、没登录
         String cartKey =CART_PREFIX+userInfoTo.getUserKey();
            //获取临时购物车的所有购物项
            List<CartItemVo> cartItems = getCartItems(cartKey);
            cartVo.setItems(cartItems);
        }

        return cartVo;
    }



    /**
     * 获取到我们要操作的购物车
     * @return
     */
    private BoundHashOperations<String,Object,Object> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();

        String cartKey="";
        if(userInfoTo.getUserId()!=null){
            cartKey=CART_PREFIX+userInfoTo.getUserId();
        }else{
            cartKey=CART_PREFIX+userInfoTo.getUserKey();
        }

        BoundHashOperations<String,Object,Object> operations = redisTemplate.boundHashOps(cartKey);

        return operations;
    }

    private List<CartItemVo> getCartItems(String cartKey){
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(cartKey);
        List<Object> values = hashOps.values();
        if(values!=null&&values.size()>0){
            List<CartItemVo> collect = values.stream().map((obj) -> {
                String str = (String) obj;
                CartItemVo cartItemVo = JSON.parseObject(str, CartItemVo.class);

                return cartItemVo;
            }).collect(Collectors.toList());

            return collect;
        }

        return  null;
    }

    @Override
    public void clearCart(String cartKey) {
        redisTemplate.delete(cartKey);
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItemVo cartItem = getCartItem(skuId);
        cartItem.setCheck(check==1?true:false);
        String s = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(),s);
    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {
        CartItemVo cartItem = getCartItem(skuId);
        cartItem.setCount(num);

        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(),JSON.toJSONString(cartItem));
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    @Override
    public List<CartItemVo> getUserCartItems() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if(userInfoTo.getUserId()==null){
            return null;
        }else{
           String cartKey=CART_PREFIX+userInfoTo.getUserId();
            List<CartItemVo> cartItems = getCartItems(cartKey);

            //获取被选中的购物项
            List<CartItemVo> collect = cartItems.stream().filter(item -> item.getCheck()).map(item->{
                //更新为最新价格
                R price = productFeignService.getPrice(item.getSkuId());
                String data = (String) price.get("data");
                item.setPrice(new BigDecimal(data));
                return item;
            }).collect(Collectors.toList());
            return collect;
        }

    }
}

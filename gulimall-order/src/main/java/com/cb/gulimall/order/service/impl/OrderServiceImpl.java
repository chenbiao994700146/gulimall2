package com.cb.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.cb.common.exception.NoStockException;
import com.cb.common.to.mq.OrderTo;
import com.cb.common.to.mq.SeckillOrderTo;
import com.cb.common.utils.R;
import com.cb.common.vo.MemberRespVo;
import com.cb.gulimall.order.constant.OrderConstant;
import com.cb.gulimall.order.dao.OrderItemDao;
import com.cb.gulimall.order.entity.OrderItemEntity;
import com.cb.gulimall.order.entity.PaymentInfoEntity;
import com.cb.gulimall.order.enume.OrderStatusEnum;
import com.cb.gulimall.order.feign.CartFeignService;
import com.cb.gulimall.order.feign.MemberFeignService;
import com.cb.gulimall.order.feign.ProductFeignService;
import com.cb.gulimall.order.feign.WmsFeignService;
import com.cb.gulimall.order.interceptor.LoginUserInterceptor;
import com.cb.gulimall.order.service.OrderItemService;
import com.cb.gulimall.order.service.PaymentInfoService;
import com.cb.gulimall.order.vo.*;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cb.common.utils.PageUtils;
import com.cb.common.utils.Query;

import com.cb.gulimall.order.dao.OrderDao;
import com.cb.gulimall.order.entity.OrderEntity;
import com.cb.gulimall.order.service.OrderService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import to.OrderCreateTo;

import javax.annotation.Resource;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    private ThreadLocal<OrderSubmitVo> confirmVoThreadLocal = new ThreadLocal<>();

    @Resource
    RabbitTemplate rabbitTemplate;

    @Resource
    OrderItemService orderItemService;

    @Resource
    MemberFeignService memberFeignService;

    @Resource
    CartFeignService cartFeignService;

    @Resource
    WmsFeignService wmsFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Resource
    ProductFeignService productFeignService;

    @Resource
    PaymentInfoService paymentInfoService;

    @Resource
    ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        System.out.println("主线程。。。。" + Thread.currentThread().getId());
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        CompletableFuture<Void> getAddressFutrue = CompletableFuture.runAsync(() -> {
            //没一个线程都来共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            System.out.println("getAddressFutrue副线程。。。。" + Thread.currentThread().getId());
            //1、远程查询所有收货地址列表
            List<MemberAddressVo> address = memberFeignService.getAddress(memberRespVo.getId());
            confirmVo.setAddress(address);

        }, executor);


        CompletableFuture<Void> getItensFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            System.out.println("getItensFuture副线程。。。。" + Thread.currentThread().getId());
            //2、远程查询购物车所有选中的购物项
            List<OrderItemVo> cartItems = cartFeignService.getCurrentuserCartItems();
            confirmVo.setItems(cartItems);
            //fegin在远程调用之前要构造请求，调用很多的拦截器
            //RequestInterceptor interceptor : requestInterceptors
        }, executor).thenRunAsync(() -> {
            List<OrderItemVo> items = confirmVo.getItems();
            List<Long> collect = items.stream().map(item -> item.getSkuId()).collect(Collectors.toList());
            R hasStock = wmsFeignService.getSkuHasStock(collect);
            List<SkuStockVo> data = hasStock.getData(new TypeReference<List<SkuStockVo>>() {
            });

            if (data != null) {
                Map<Long, Boolean> map = data.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
                confirmVo.setStocks(map);
            }

        }, executor);


        //3、查询用户积分
        Integer integration = memberRespVo.getIntegration();
        confirmVo.setIntegration(integration);

        //4、其他数据自动计算


        //5、TODO 防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId(), token, 30, TimeUnit.MINUTES);
        confirmVo.setOrderToken(token);

        CompletableFuture.allOf(getAddressFutrue, getItensFuture).get();
        return confirmVo;
    }

//    @Transactional
//    public void a(){
//
//        bService.b();
//        cService.c();
//    }
//    @Transactional(propagation = Propagation.REQUIRED)
//    public void b(){
    //   bServiceImpl   bService=  (bServiceImpl) AopContext.currentProxy();
//    }
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public void c(){
//
//    }


    //本地事务，在分布式系统，只能控制自己的回滚，控制不了其他服务的回滚
    //分布式事务：最大问题，网络问题
    //@GlobalTransactional
    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) {
        confirmVoThreadLocal.set(vo);
        SubmitOrderResponseVo response = new SubmitOrderResponseVo();
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        response.setCode(0);

        //1、验证令牌【令牌的对比和删除必须保证原子性】
        //0令牌失败   1删除成功
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String orderToken = vo.getOrderToken();
        //原子验证令牌和删除令牌
        Long result = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId()), orderToken);
        if (result == 0L) {
            //令牌验证失败
            response.setCode(1);
            return response;
        } else {
            //令牌验证成功
            //下单：去创建订单，验价格，锁库存
            //1、创建订单
            OrderCreateTo order = createOrder();
            //2验价
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = vo.getPayPrice();
            if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
                //金额对比、

                //TODO 3、保存订单
                saveOrder(order);
                //、库存锁定，只要有异常回滚订单数据
                //订单号，所有订单项（skuId,skuName,num）
                WareSkuLockVo lockVo = new WareSkuLockVo();
                lockVo.setOrderSn(order.getOrder().getOrderSn());
                List<OrderItemVo> locks = order.getOrderItems().stream().map(item -> {
                    OrderItemVo itemVo = new OrderItemVo();
                    itemVo.setSkuId(item.getSkuId());
                    itemVo.setCount(item.getSkuQuantity());
                    itemVo.setTitle(item.getSkuName());
                    return itemVo;
                }).collect(Collectors.toList());
                lockVo.setLocks(locks);
                //TODO 4、远程锁库存
                //为了包装高并发。库存服务自己回滚。可以发送消息给库存服务
                //库存服务本身也可以使用自动解锁模式，消息队列
                R r = wmsFeignService.orderLockStock(lockVo);
                if (r.getCode() == 0) {

                    //锁成功
                    response.setOrder(order.getOrder());

                    //TODO 5、远程扣减积分
                    //int i=10/0;
                    //TODO 订单创建成功发送消息MQ
                    rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", order.getOrder());
                    return response;
                } else {
                    //锁失败
                    String msg = (String) r.get("msg");
                    throw new NoStockException(4L);
                    //return response;
                }
            } else {
                response.setCode(2);
                return response;
            }

        }


//        String s = redisTemplate.opsForValue().get(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId());
//        if(orderToken!=null&& orderToken.equals(s)){
//            //令牌验证通过
//            redisTemplate.delete(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId());
//        }else{
//            //不通过
//        }

    }

    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        OrderEntity entity = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        return entity;
    }

    @Override
    public void closeOrder(OrderEntity orderEntity) {
        //查询当前这个订单的最新状态
        OrderEntity entity = this.getById(orderEntity.getId());
        if (entity.getStatus() == OrderStatusEnum.CREATE_NEW.getCode()) {
            //关单
            OrderEntity update = new OrderEntity();
            update.setId(orderEntity.getId());
            update.setStatus(OrderStatusEnum.CANCLED.getCode());
            this.updateById(update);
            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(entity, orderTo);
            //发送给MQ一个
            try {
                //TODO 保证消息一定会发送出去，没一个消息都可以做好日志记录（给数据库保持没一个消息的详细信息）。
                //TODO 定期扫描数据库将失败的消息再发送一遍。
                rabbitTemplate.convertAndSend("order-event-exchange", "order.release.other", orderTo);
            } catch (Exception e) {
                //TODO 将没发送成功的消息进行重试发送。
            }

        }
    }

    /**
     * 获取当前订单的支付信息
     *
     * @param orderSn
     * @return
     */
    @Override
    public PayVo getOrderPay(String orderSn) {
        PayVo payVo = new PayVo();
        OrderEntity order = this.getOrderByOrderSn(orderSn);

        BigDecimal bigDecimal = order.getPayAmount().setScale(2, BigDecimal.ROUND_UP);
        payVo.setTotal_amount(bigDecimal.toString());
        payVo.setOut_trade_no(order.getOrderSn());

        List<OrderItemEntity> order_sn = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
        OrderItemEntity entity = order_sn.get(0);
        payVo.setSubject(entity.getSkuName());
        payVo.setBody(entity.getSkuAttrsVals());


        return payVo;
    }

    @Override
    public PageUtils queryPageWithItem(Map<String, Object> params) {
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();

        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>().eq("member_id", memberRespVo.getId()).orderByDesc("id")
        );

        List<OrderEntity> order_sn = page.getRecords().stream().map(order -> {
            List<OrderItemEntity> itemEntities = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", order.getOrderSn()));
            order.setItemEntities(itemEntities);
            return order;
        }).collect(Collectors.toList());

        page.setRecords(order_sn);

        return new PageUtils(page);
    }

    /**
     * 处理支付宝的支付结果
     *
     * @param vo
     * @return
     */
    @Override
    public String handlePayResult(PayAsyncVo vo) {
        PaymentInfoEntity infoEntity = new PaymentInfoEntity();
        infoEntity.setAlipayTradeNo(vo.getTrade_no());
        infoEntity.setOrderSn(vo.getOut_trade_no());
        infoEntity.setPaymentStatus(vo.getTrade_status());
        infoEntity.setCallbackTime(vo.getNotify_time());

        paymentInfoService.save(infoEntity);

        //2、修改订单的状态信息
        if (vo.getTrade_status().equals("TRADE_SUCCESS") || vo.getTrade_status().equals("TRADE_FINISHED")) {
            //支付成功状态
            String outTradeNo = vo.getOut_trade_no();
            this.baseMapper.updateOrderStatus(outTradeNo, OrderStatusEnum.PAYED.getCode());
        }

        return "success";
    }

    @Override
    public void createSeck(SeckillOrderTo seckillOrderTo) {
        //TODO 保存订单信息
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(seckillOrderTo.getOrderSn());
        orderEntity.setMemberId(seckillOrderTo.getMemberId());
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());

        BigDecimal multiply = seckillOrderTo.getSeckillPrice().multiply(new BigDecimal("" + seckillOrderTo.getNum()));
        orderEntity.setPayAmount(multiply);
        this.save(orderEntity);

        //TODO 保存订单信息
        OrderItemEntity entity = new OrderItemEntity();
        entity.setOrderSn(seckillOrderTo.getOrderSn());
        entity.setRealAmount(multiply);
        //TODO 获取当前SKU的详细信息进行设置  productFeignService.getSpuInfoBySkuId()
        entity.setSkuQuantity(seckillOrderTo.getNum());

        orderItemService.save(entity);

    }


    /**
     * 保存订单数据
     *
     * @param order
     */
    private void saveOrder(OrderCreateTo order) {
        OrderEntity orderEntity = order.getOrder();
        orderEntity.setModifyTime(new Date());
        this.save(orderEntity);

        List<OrderItemEntity> orderItems = order.getOrderItems();
        orderItemService.saveBatch(orderItems);
    }

    private OrderCreateTo createOrder() {
        OrderCreateTo createTo = new OrderCreateTo();
        //1、生成订单号
        String orderSn = IdWorker.getTimeId();
        //创建订单号
        OrderEntity orderEntity = bulidOrder(orderSn);


        //2、获取到所有的订单项
        List<OrderItemEntity> itemEntities = buildOrderItems(orderSn);

        //3、验价(计算价格相关)
        computePrice(orderEntity, itemEntities);
        createTo.setOrder(orderEntity);
        createTo.setOrderItems(itemEntities);
        return createTo;
    }

    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> itemEntities) {
        BigDecimal total = new BigDecimal("0.0");

        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal integration = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");

        BigDecimal gift = new BigDecimal("0.0");
        BigDecimal growth = new BigDecimal("0.0");
        //订单的总额，叠加没一个订单项的总额信息
        for (OrderItemEntity entity : itemEntities) {
            coupon = coupon.add(entity.getCouponAmount());
            integration = integration.add(entity.getIntegrationAmount());
            promotion = promotion.add(entity.getPromotionAmount());
            total = total.add(entity.getRealAmount());
            gift = gift.add(new BigDecimal(entity.getGiftIntegration().toString()));
            growth = growth.add(new BigDecimal(entity.getGiftGrowth().toString()));
        }
        //1、订单价格相关
        orderEntity.setTotalAmount(total);
        //应付总额
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(integration);
        orderEntity.setCouponAmount(coupon);

        //设置积分信息
        orderEntity.setIntegration(gift.intValue());
        orderEntity.setGrowth(growth.intValue());
        orderEntity.setDeleteStatus(0);//未删除

    }

    private OrderEntity bulidOrder(String orderSn) {
        MemberRespVo respVo = LoginUserInterceptor.loginUser.get();
        OrderEntity entity = new OrderEntity();
        entity.setOrderSn(orderSn);
        entity.setMemberId(respVo.getId());
        OrderSubmitVo submitVo = confirmVoThreadLocal.get();
        //获取收货地址信息
        R fare = wmsFeignService.getFare(submitVo.getAddrId());
        FareVo fareResp = fare.getData(new TypeReference<FareVo>() {
        });
        //设置运费信息
        entity.setFreightAmount(fareResp.getFare());
        //设置收货人信息
        entity.setReceiverCity(fareResp.getAddress().getCity());
        entity.setReceiverDetailAddress(fareResp.getAddress().getDetailAddress());
        entity.setReceiverName(fareResp.getAddress().getName());
        entity.setReceiverPhone(fareResp.getAddress().getPhone());
        entity.setReceiverPostCode(fareResp.getAddress().getPostCode());
        entity.setReceiverProvince(fareResp.getAddress().getProvince());
        entity.setReceiverRegion(fareResp.getAddress().getRegion());
        entity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        return entity;
    }

    /**
     * 构建所有订单项数据
     *
     * @return
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) {

        List<OrderItemVo> currentuserCartItems = cartFeignService.getCurrentuserCartItems();
        if (currentuserCartItems != null && currentuserCartItems.size() > 0) {
            List<OrderItemEntity> itemEntities = currentuserCartItems.stream().map(cartItem -> {
                OrderItemEntity itemEntity = buildOrderItem(cartItem);
                itemEntity.setOrderSn(orderSn);
                return itemEntity;
            }).collect(Collectors.toList());

            return itemEntities;
        }
        return null;
    }

    /**
     * 构建某一个订单项
     *
     * @param cartItem
     * @return
     */
    private OrderItemEntity buildOrderItem(OrderItemVo cartItem) {
        OrderItemEntity itemEntity = new OrderItemEntity();
        //1、订单信息 v
        //2、商品的SPU信息
        Long skuId = cartItem.getSkuId();
        R r = productFeignService.getSpuInfoBySkuId(skuId);
        SpuInfoVo data = r.getData(new TypeReference<SpuInfoVo>() {
        });
        itemEntity.setSpuId(data.getId());
        itemEntity.setSpuBrand(data.getBrandId().toString());
        itemEntity.setSpuName(data.getSpuName());
        itemEntity.setCategoryId(data.getCatalogId());
        //3、商品的sku信息 v
        itemEntity.setSkuId(cartItem.getSkuId());
        itemEntity.setSkuName(cartItem.getTitle());
        itemEntity.setSkuPic(cartItem.getImage());
        itemEntity.setSkuPrice(cartItem.getPrice());
        itemEntity.setSkuQuantity(cartItem.getCount());
        String skuAttr = StringUtils.collectionToDelimitedString(cartItem.getSkuAttrValues(), ";");
        itemEntity.setSkuAttrsVals(skuAttr);
        //4、优惠信息(不做)
        //5、积分信息
        itemEntity.setGiftGrowth(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());
        itemEntity.setGiftIntegration(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());
        //6、订单项的价格信息
        itemEntity.setPromotionAmount(new BigDecimal("0"));
        itemEntity.setCouponAmount(new BigDecimal("0"));
        itemEntity.setIntegrationAmount(new BigDecimal("0"));
        //当前订单下个的实际金额
        BigDecimal orgin = itemEntity.getSkuPrice().multiply(new BigDecimal(itemEntity.getSkuQuantity().toString()));
        BigDecimal subtract = orgin.subtract(itemEntity.getCouponAmount())
                .subtract(itemEntity.getIntegrationAmount())
                .subtract(itemEntity.getPromotionAmount());
        itemEntity.setRealAmount(subtract);
        return itemEntity;
    }

}
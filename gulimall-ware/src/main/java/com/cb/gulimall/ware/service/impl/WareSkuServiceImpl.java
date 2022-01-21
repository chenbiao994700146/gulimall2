package com.cb.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.cb.common.exception.NoStockException;
import com.cb.common.to.mq.OrderTo;
import com.cb.common.to.mq.StockDetailTo;
import com.cb.common.to.mq.StockLockedTo;
import com.cb.common.utils.R;
import com.cb.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.cb.gulimall.ware.entity.WareOrderTaskEntity;
import com.cb.gulimall.ware.feign.OrderFeignService;
import com.cb.gulimall.ware.feign.ProductFeignService;
import com.cb.gulimall.ware.service.WareOrderTaskDetailService;
import com.cb.gulimall.ware.service.WareOrderTaskService;
import com.cb.gulimall.ware.vo.OrderItemVo;
import com.cb.gulimall.ware.vo.OrderVo;
import com.cb.gulimall.ware.vo.SkuHasStockVo;
import com.cb.gulimall.ware.vo.WareSkuLockVo;
import com.rabbitmq.client.Channel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cb.common.utils.PageUtils;
import com.cb.common.utils.Query;

import com.cb.gulimall.ware.dao.WareSkuDao;
import com.cb.gulimall.ware.entity.WareSkuEntity;
import com.cb.gulimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Slf4j
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Resource
    WareSkuDao wareSkuDao;

    @Resource
    ProductFeignService productFeignService;

    @Resource
    WareOrderTaskService orderTaskService;

    @Resource
    WareOrderTaskDetailService orderTaskDetailService;

    @Resource
    OrderFeignService orderFeignService;

    @Resource
    RabbitTemplate rabbitTemplate;



    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /**
         *
         * skuId: 1
         * wareId: 2
         */

        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if(!StringUtils.isEmpty(skuId)){
            wrapper.eq("sku_id",skuId);
        }

        String wareId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(skuId)){
            wrapper.eq("ware_id",wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1.判断如果还没有库存记录 新增
        List<WareSkuEntity> entities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));

        if(entities==null||entities.size()==0){
            WareSkuEntity skuEntity = new WareSkuEntity();

            skuEntity.setSkuId(skuId);
            skuEntity.setStock(skuNum);
            skuEntity.setWareId(wareId);
            skuEntity.setStockLocked(0);
            //TODO 远程查询名字,如果失败不回滚
            //1.自己catch异常
            try{
                R info = productFeignService.info(skuId);
                Map<String,Object> data = (Map<String, Object>) info.get("skuInfo");

                if(info.getCode()==0){
                    skuEntity.setSkuName((String) data.get("skuName"));
                }
            }catch (Exception e){

            }

            wareSkuDao.insert(skuEntity);
        }

        wareSkuDao.addStock(skuId,wareId,skuNum);
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        List<SkuHasStockVo> collect = skuIds.stream().map(skuId -> {
            SkuHasStockVo vo = new SkuHasStockVo();

            //查询当前sku的总库存量
            /**
             * SELECT sum(stock-stock_locked)
             * FROM `wms_ware_sku`
             * where sku_id=1
             */
          Long count=  baseMapper.getSkuStcok(skuId);
          vo.setSkuId(skuId);
          vo.setHasStock(count==null?false:count>0);
            return vo;
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 为某个订单锁定库存
     * 默认只要是运行是异常都会回滚
     *
     * 库存解锁的场景
     * 1）、下订单成功，订单过期没有支付被系统自动取消，被用户手动取消，都要解锁库存
     *
     * 2）、下订单成功，库存锁定成功，接下来的业务调用失败，导致订单回滚，之前锁定的库存就要自动解锁。
     *
     *
     * @param vo
     * @return
     */
    @Transactional
    @Override
    public Boolean orderLockStock(WareSkuLockVo vo) {

        /**
         * 保存库存工作单的详情
         * 追溯
         */
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(vo.getOrderSn());
        orderTaskService.save(taskEntity);


        //1、按照下单的收货地址，找到一个就近仓库，锁定库存
        //1、找到每个商品在那个仓库都有库存
        List<OrderItemVo> locks = vo.getLocks();
        List<SkuWareHasStock> collect = locks.stream().map(item -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            stock.setNum(item.getCount());
            stock.setSkuId(skuId);
            //查询这个商品在哪里有库存
         List<Long> wareIds= wareSkuDao.listWareIdHasSkuStock(skuId);
         stock.setWareId(wareIds);
            return stock;
        }).collect(Collectors.toList());

        Boolean skuStocked=true;
        //2、锁定库存
        for (SkuWareHasStock hasStock : collect) {
             skuStocked=false;
            Long skuId = hasStock.getSkuId();
            List<Long> wareIds = hasStock.getWareId();
            if (wareIds==null||wareIds.size()==0){
                //没有任何仓库有这个商品的库存
                throw  new NoStockException(skuId);
            }

            /**
             * 1、如果没一个商品都锁定成功，将当前商品锁定了几件的工作单记录发送给MQ
             * 2、锁定失败，前面保存的工作单信息就回滚了，发送出去的消息，即使要解锁记录，由于去数据库查不到id.,所以就不用。
             */
            for (Long wareId : wareIds) {
                //成功就返回1，否则就是0
             Long count=wareSkuDao.lockSkuStock(skuId,wareId,hasStock.getNum());
             if(count==1){
                 skuStocked=true;
                 WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity(null, skuId, "", hasStock.getNum(), taskEntity.getId(), wareId, 1);
                 orderTaskDetailService.save(entity);
                 StockLockedTo lockedTo = new StockLockedTo();
                 lockedTo.setId(taskEntity.getId());
                 StockDetailTo stockDetailTo = new StockDetailTo();
                 BeanUtils.copyProperties(entity,stockDetailTo);
                 //只发id不行，防止回滚以后找不到数据
                 lockedTo.setDetail(stockDetailTo);
                 //发送MQ
                 rabbitTemplate.convertAndSend("stock-event-exchange","stock.locked",lockedTo);
                 break;
             }else{
                //当前仓库锁失败，重试下一个仓库
             }

            }
            if(skuStocked==false){
                //当前商品所有仓库都没有锁住
                throw  new NoStockException(skuId);
            }
        }

        return skuStocked;
    }

    @Override
    public void unlockStock(StockLockedTo to) {




            StockDetailTo detail = to.getDetail();
            Long detailId = detail.getId();
            //解锁
            /**
             * 1、查询数据库关于这个订单的锁定库存信息
             *  有：证明库存锁定成功了
             *      解锁：订单情况
             *          1、没有这个订单。必须解锁
             *          2、有这个订单。不是解锁库存
             *              订单状态：已取消：解锁库存
             *                       没取消：不能解锁库存
             *  没有：库存锁定失败，库存回滚了，这种情况无需解锁
             */
            WareOrderTaskDetailEntity byId = orderTaskDetailService.getById(detailId);
            if (byId != null) {
                //解锁
                Long id = to.getId();//库存的工作单id;
                WareOrderTaskEntity taskEntity = orderTaskService.getById(id);
                String orderSn = taskEntity.getOrderSn();//根据订单号查询订单的状态
                R r = orderFeignService.getOrderStatus(orderSn);
                if (r.getCode() == 0) {
                    //订单返回数据成功

                    OrderVo data = r.getData(new TypeReference<OrderVo>() {
                    });
                    System.out.println("data的数据"+data);
                    if ( data.getStatus() == 4 ||data == null ) {
                        System.out.println("订单已经被取消了");
                        //订单已经被取消了，才能解锁库存
                        if(byId.getLockStatus()==1){
                            //当前库存工作单详情，状态1 已锁定但是未解锁的才可以解锁
                            unLockStock(detail.getSkuId(), detail.getWareId(), detail.getSkuNum(), detailId);
                        }


                    }
                } else {
                    //消息拒绝以后重新放到队列里面，让别人继续消息解锁
                    throw  new RuntimeException("远程服务失败");
                }
            } else {

            }


    }

    //防止订单服务卡顿，导致订单状态消息一直改不了，库存消息优先到期。查到订单状态新建状态，什么都不做就走了。
    //导致卡顿的订单，永远不能解锁库存
    @Transactional
    @Override
    public void unlockStock(OrderTo orderTo) {
        String orderSn = orderTo.getOrderSn();
        //查一下最新库存的状态，防止重复解锁库存
      WareOrderTaskEntity task= orderTaskService.getOrderTaskByOrderSn(orderSn);
        Long id = task.getId();
        //按照工作单找到所有  没有解锁的库存，进行解锁
        List<WareOrderTaskDetailEntity> entities = orderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>().eq("task_id", id).eq("lock_status", 1));

        for (WareOrderTaskDetailEntity entity : entities) {

            //Long skuId,Long wareId,Integer num,Long taskDetailId
            unLockStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum(),entity.getId());
        }
    }


    private void unLockStock(Long skuId,Long wareId,Integer num,Long taskDetailId){
        System.out.println("进入解锁库存方法");
        wareSkuDao.unlockStock(skuId,wareId,num);
        //更新库存工作单
        WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity();
        entity.setId(taskDetailId);
        entity.setLockStatus(2);//变为已解锁
        orderTaskDetailService.updateById(entity);
    }
    @Data
    class SkuWareHasStock{
        private Long skuId;
        private Integer num;
        private List<Long> wareId;
    }

}
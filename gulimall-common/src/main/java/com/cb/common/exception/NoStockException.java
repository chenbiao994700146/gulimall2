package com.cb.common.exception;


public class NoStockException extends RuntimeException{
    private Long skuId;
    public NoStockException(Long skuId){
        super("商品Id:"+skuId+"没有足够的库存了");
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }
}

package com.pinyougou.pojogroup;

import java.io.Serializable;
import java.util.List;

public class SeckillGoodsObject implements Serializable {
    private String sellerId;  //商家Id
    private List<Long> spuIds; //spuId列表
    private List<Long> skuIds; //skuId列表

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public List<Long> getSpuIds() {
        return spuIds;
    }

    public void setSpuIds(List<Long> spuIds) {
        this.spuIds = spuIds;
    }

    public List<Long> getSkuIds() {
        return skuIds;
    }

    public void setSkuIds(List<Long> skuIds) {
        this.skuIds = skuIds;
    }
}

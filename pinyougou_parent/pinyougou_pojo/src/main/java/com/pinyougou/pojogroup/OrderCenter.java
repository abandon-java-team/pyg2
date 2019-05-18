package com.pinyougou.pojogroup;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;

import java.io.Serializable;
import java.util.List;

public class OrderCenter implements Serializable {
    private String nickName;
    private TbOrder order;
    private List<TbOrderItem> tbOrderItems;

    public String getNickName() {return nickName; }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public TbOrder getOrder() {
        return order;
    }
    public void setOrder(TbOrder order) { this.order = order; }
    public List<TbOrderItem> getTbOrderItems() {
        return tbOrderItems;
    }
    public void setTbOrderItems(List<TbOrderItem> tbOrderItems) {
        this.tbOrderItems = tbOrderItems;
    }
}

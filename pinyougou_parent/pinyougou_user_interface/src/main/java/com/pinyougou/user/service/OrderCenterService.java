package com.pinyougou.user.service;

import com.pinyougou.pojogroup.OrderCenter;

import java.util.List;

public interface OrderCenterService {
    /**
     * 根据订单的状态查询用户的订单信息
     * @param userId 用户的名称
     * @param status 订单状态
     * @return
     */
    List<OrderCenter> findOrderByStatus(String userId, String status);
}

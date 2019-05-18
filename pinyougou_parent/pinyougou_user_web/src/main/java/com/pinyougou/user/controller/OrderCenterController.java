package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojogroup.OrderCenter;
import com.pinyougou.user.service.OrderCenterService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("orderCenter")
public class OrderCenterController {
    @Reference
    private OrderCenterService orderCenterService;

    @RequestMapping("orderInfo")
    public List<OrderCenter> findOrderByStatus(String status) {
        //获取登入的用户名称
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<OrderCenter> orderCenters = orderCenterService.findOrderByStatus(userId,status);
        return orderCenters;
    }
}

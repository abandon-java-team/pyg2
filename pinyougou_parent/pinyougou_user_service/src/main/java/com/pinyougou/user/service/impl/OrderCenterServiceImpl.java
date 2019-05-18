package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.abel533.entity.Example;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.pojogroup.OrderCenter;
import com.pinyougou.user.service.OrderCenterService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Service(timeout = 5000)
public class OrderCenterServiceImpl implements OrderCenterService {
    @Autowired
    private TbOrderMapper orderMapper;
    @Autowired
    private TbOrderItemMapper tbOrderItemMapper;
    @Autowired
    private TbSellerMapper sellerMapper;
    @Override
    public List<OrderCenter> findOrderByStatus(String userId, String status) {
        //创建订单集合
        List<OrderCenter> orderCenterList = new ArrayList<>();
        //通过用户的Id加载订单选项
        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        List<TbOrder> orderList = orderMapper.selectByExample(example);
        if (orderList != null && orderList.size()>0) {  //判断集合是否数据
            for (TbOrder tbOrder : orderList) {
                OrderCenter orderCenter =null;
                //获取用户订单的状态信息
                String orderStatus = tbOrder.getStatus();
                //根据orderId查询订单详情
                Long orderId = tbOrder.getOrderId();
                Example example1 = new Example(TbOrderItem.class);
                example1.createCriteria().andEqualTo("orderId",orderId);
                List<TbOrderItem> orderItemList = tbOrderItemMapper.selectByExample(example1);
                //查询店铺的名称
                TbSeller tbSeller = sellerMapper.selectByPrimaryKey(tbOrder.getSellerId());
                String nickName = tbSeller.getNickName();
                //判断状态信息
                if("selectedStatus".equals(status)){ //查询全部的状态的信息
                    orderCenter=new OrderCenter();
                    orderCenter.setNickName(nickName);
                    orderCenter.setOrder(tbOrder);
                    orderCenter.setTbOrderItems(orderItemList);
                }
                if(orderStatus.equals(status)){  //查询所有的有前台页面的状态信息
                    orderCenter=new OrderCenter();
                    orderCenter.setNickName(nickName);
                    orderCenter.setOrder(tbOrder);
                    orderCenter.setTbOrderItems(orderItemList);
                }
                if(orderCenter!=null){
                    orderCenterList.add(orderCenter);
                }
            }
        }
        return orderCenterList;
    }
}

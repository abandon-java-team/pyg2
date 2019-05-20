package com.pinyougou.shop.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import entity.PageResult;
import entity.Result;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 请求处理器
 *
 * @author Steven
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbOrder> findAll() {
        return orderService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return orderService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param order
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbOrder order) {
        try {
            //用户id
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            order.setUserId(userId);
            //订单来源
            order.setSourceType("2");
            orderService.add(order);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param order
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbOrder order) {
        try {
            orderService.update(order);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public TbOrder findOne(Long id) {
        return orderService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            orderService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param order
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbOrder order, int page, int rows) {
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        return orderService.findPage(order, page, rows, sellerId);
    }


    //发货
    @RequestMapping("/deliverGoods")
    public Result deliverGoods(String orderIdStr){
        Result result = null;
        try {
            Long orderId = new Long(orderIdStr);
            orderService.deliverGoods(orderId);
            result = new Result(true,"已发货");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            result = new Result(false, "发货失败");
        }
        return result;
    }

    /**订单统计
     * @param
     * @param
     * @return
     */
   /* @RequestMapping("accountByGoods")
    public Map accountByGoods(Date startTime, Date endTime){
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        Map map = orderService.accountByGoods(sellerId, startTime, endTime);
        return map;
    }*/
    @RequestMapping("accountByGoods")
    public Map accountByGoods(){
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        Map map = orderService.accountByGoods(sellerId);
        return map;
    }
}

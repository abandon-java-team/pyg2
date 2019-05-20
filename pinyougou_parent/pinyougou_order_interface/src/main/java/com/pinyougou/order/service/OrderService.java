package com.pinyougou.order.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbOrder;

import com.pinyougou.pojo.TbPayLog;
import entity.PageResult;

/**
 * 业务逻辑接口
 *
 * @author Steven
 */
public interface OrderService {

    /**
     * 返回全部列表
     *
     * @return
     */
    public List<TbOrder> findAll();


    /**
     * 返回分页列表
     *
     * @return
     */
    public PageResult findPage(int pageNum, int pageSize);


    /**
     * 增加
     */
    public void add(TbOrder order);


    /**
     * 修改
     */
    public void update(TbOrder order);


    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    public TbOrder findOne(Long id);


    /**
     * 批量删除
     *
     * @param ids
     */
    public void delete(Long[] ids);

    /**
     * 分页
     *
     * @param pageNum  当前页 码
     * @param pageSize 每页记录数
     * @return
     */
    public PageResult findPage(TbOrder order, int pageNum, int pageSize,String sellerId);
    public PageResult findPage(TbOrder order, int pageNum, int pageSize);

    /**
     * 根据用户查询payLog
     *
     * @param userId
     * @return
     */
    public TbPayLog searchPayLogFromRedis(String userId);

    /**
     * 修改订单状态
     *
     * @param out_trade_no   支付订单号
     * @param transaction_id 微信返回的交易流水号
     */
    public void updateOrderStatus(String out_trade_no, String transaction_id);


    /**
     * 发货
     * @param orderId 订单id
     */
    public void deliverGoods(Long orderId);

    /**
     * 指定时间段统计各商品销售额
     * @param sellerId
     * @param startTime
     * @param endTime
     * @return
     */
    public Map accountByGoods(String sellerId, Date startTime, Date endTime);

    /*@Override
    public Map accountByGoods(String sellerId, Date startTime, Date endTime){
        Map map = new HashMap();
        List<TbOrder> orderList = null;
        //sellerId 在tb_item查询商品id
        TbItem where = new TbItem();
        where.setSellerId(sellerId);
        List<TbItem> itemList = itemMapper.select(where);
        //商品id在tb_order_item查询订单号
        for (TbItem item : itemList) {
            //每个商品 多个订单
            Long itemId = item.getId();
            TbOrderItem where1 = new TbOrderItem();
            where1.setItemId(itemId);
            List<TbOrderItem> orderItems = orderItemMapper.select(where1);
            for (TbOrderItem orderItem : orderItems) {
                //获取订单详情
                TbOrder where2 = new TbOrder();
                where2.setOrderId(orderItem.getOrderId());
                TbOrder order = orderMapper.selectByPrimaryKey(where2);
                if (order.getEndTime().getTime() > endTime.getTime() && startTime.getTime() > order.getEndTime().getTime()){
                    orderList.add(order);
                }
            }
            map.put(itemId, orderList);
            orderList.clear();
        }
        return map;
    }*/

    Map accountByGoods(String sellerId);
}

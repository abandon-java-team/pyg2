package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.List;

/**
 * 请求处理器
 *
 * @author Steven
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;


    @Autowired
    private JmsTemplate jmsTemplate;


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return goodsService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param goods
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody Goods goods) {
        try {
            //记录商家
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            goods.getGoods().setSellerId(sellerId);

            goodsService.add(goods);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param goods
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody Goods goods) {
        try {
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            //从数据库先把将要更新的商品信息查询出来
            Goods beUpdate = goodsService.findOne(goods.getGoods().getId());
            //如果当前要修改的商品不属于当前登录的商家
            if (!sellerId.equals(beUpdate.getGoods().getSellerId())) {
                return new Result(false, "请注意你的言行，这是一个非法操作!");
            }
            goodsService.update(goods);
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
    public Goods findOne(Long id) {
        return goodsService.findOne(id);
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
            goodsService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param goods
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbGoods goods, int page, int rows) {
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(sellerId);
        return goodsService.findPage(goods, page, rows);
    }

    @Autowired
    private Destination queueSolrDestination;  //索引库更新队列
    @Autowired
    private Destination queueSolrDeleteDestination;  //索引库删除队列
    @Autowired
    private Destination topicPageDestination;  //生成静态页面
    @Autowired
    private Destination topicPageDeleteDestination;  //删除静态页面队列

    /**
     * 商家点击商品上架,修改数据库上架状态
     * <p>
     * 然后发送MQ消息点对点消息,生成静态页面
     */
    @RequestMapping("/addMarketable")
    public Result addMarketable(Long[] goodsIds, String marketable) {

        try {
            //先查询所有商品sku列表
            List<TbItem> itemList = goodsService.findItemListByGoodsIds(goodsIds);
            List<TbGoods> goodsList = goodsService.findGoodsListByGoodsIds(goodsIds);

            for (TbGoods tbGoods : goodsList) {
                if (!tbGoods.getAuditStatus().equals("1")) {
                    throw new RuntimeException("未审核的商品不能上架！");
                }
                if (tbGoods.getIsMarketable().equals("1")) {
                    throw new RuntimeException("已上架的商品不能重复上架！");
                }
            }
            goodsService.updateMarketable(goodsIds, marketable);

            //发送消息到MQ
            String jsonItem = JSON.toJSONString(itemList);
            jmsTemplate.send(queueSolrDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createTextMessage(jsonItem);
                }
            });

            //发消息生成商品详情页
            jmsTemplate.send(topicPageDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createObjectMessage(goodsIds);
                }
            });

            return new Result(true, "商品上架成功");
        } catch (RuntimeException e) {
            return new Result(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, "商品上架失败");
    }

    @RequestMapping("/deleteMarketable")
    public Result deleteMarketable(Long[] goodsIds, String marketable) {

        try {
            //先查询所有商品sku列表
            List<TbItem> itemList = goodsService.findItemListByGoodsIds(goodsIds);
            List<TbGoods> goodsList = goodsService.findGoodsListByGoodsIds(goodsIds);

            for (TbGoods tbGoods : goodsList) {

                if (tbGoods.getIsMarketable().equals("0")) {
                    throw new RuntimeException("选中商品尚未上架！");
                }
            }
            goodsService.updateMarketable(goodsIds, marketable);

            //发送消息到MQ删除solr
            String jsonItem = JSON.toJSONString(itemList);
            jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createTextMessage(jsonItem);
                }
            });

            //发消息删除商品详情页
            jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createObjectMessage(goodsIds);
                }
            });
            return new Result(true, "商品下架成功");
        } catch (RuntimeException e) {
            return new Result(false, e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "商品下架失败");
        }

    }
}


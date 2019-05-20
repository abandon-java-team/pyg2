package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.utils.CookieUtil;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/collect")
public class CollectController {

    @Reference
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;

    /**
     * 根据itemId查找商品并添加到收藏夹
     * @param itemId
     * @return
     */
    @RequestMapping("/addGoodsToCollect")
//    @CrossOrigin(origins = "http://localhost:8088")
    public Result addGoodsToCollect(Long itemId) {
        try {
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            //如果原来没有收藏夹，准备一个空集合
            List<TbItem> collect = new ArrayList<>();
            //添加收藏夹
            collect = cartService.addGoodsToCollect(itemId);

            //未登录
            if ("anonymousUser".equals(userName)) {
                return new Result(false, "请登录后再收藏");
            } else {
                //把数据保存到Redis中
                cartService.saveCollectToRedis(userName, collect);
                System.out.println("操作了Redis中的收藏夹数据...");
            }
            return new Result(true, "收藏成功！");
        } catch (RuntimeException e) {
            //返回自定义消息
            return new Result(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, "收藏失败！");
    }

    /**
     *从redis中加载收藏夹
     * @return
     */
    @RequestMapping("/findCollect")
     public List<TbItem> findCollect(){
         String userName = SecurityContextHolder.getContext().getAuthentication().getName();
         List<TbItem> collect = new ArrayList<>();
         collect = cartService.findCollectFromRedis(userName);
         return collect;
     }

    /**
     * 查找当前购物车
     * @return
     */
    @RequestMapping("findCartList")
    public List<Cart> findCartList() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        //如果原来没有购物车，准备一个空集合
        List<Cart> cartList = new ArrayList<>();
        //未登录，查询cookie
            //查询redis数据
            List<Cart> redisList = cartService.findCartListFromRedis(userName);
            System.out.println("从Redis中获取了购物车数据...");
        return cartList;
    }

    /**
     * 从收藏页面个根据itemID加入到购物车
     * @param itemId
     * @return
     */
     @RequestMapping("/addGoodsToCartListFromCollect")
     public Result addGoodsToCartList (Long itemId){
         try {
             String userName = SecurityContextHolder.getContext().getAuthentication().getName();
             //先查询原来的购物车列表
             List<Cart> cartList = this.findCartList();
             //添加购物车
             cartList = cartService.addGoodsToCartList(cartList, itemId, 1);
             cartService.saveCartListToRedis(userName, cartList);
             System.out.println("操作了Redis中的购物车数据...");
             return new Result(true, "购物操作成功！");
         }catch (RuntimeException e) {
             //返回自定义消息
             return new Result(false, e.getMessage());
         } catch (Exception e) {
             e.printStackTrace();
         }
         return new Result(false, "购物添加失败！");
     }

    /**
     * success-cart页面回显
     */
    @RequestMapping("/findByItemId")
    public TbItem findByItemId(Long itemId){
        TbItem item = cartService.findByItemId(itemId);
        return item;
    }
}

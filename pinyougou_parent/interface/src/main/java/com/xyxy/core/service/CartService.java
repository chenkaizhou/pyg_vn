package com.xyxy.core.service;

import com.xyxy.core.pojo.entity.BuyerCart;

import java.util.List;

public interface CartService {
    //添加商品到购物车
    public List<BuyerCart> addItemToCartList(List<BuyerCart> cartList, Long itemId, Integer num);
    //从redis中获取购物车
    public void setCartListToRedis(String userName, List<BuyerCart> cartList);
    //将购物车保存到redis中  登录后
    public List<BuyerCart> getCartListFromRedis(String userName);
    //将cookie中取出的购物车于redis中的购物车合并保存到redis中
    public List<BuyerCart> mergeCookieCartListToRedisCartList(List<BuyerCart> cookieCartList, List<BuyerCart> redisCartList);
}

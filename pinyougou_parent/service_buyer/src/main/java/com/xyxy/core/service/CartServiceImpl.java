package com.xyxy.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.xyxy.core.dao.item.ItemDao;
import com.xyxy.core.pojo.entity.BuyerCart;
import com.xyxy.core.pojo.item.Item;
import com.xyxy.core.pojo.order.OrderItem;
import com.xyxy.core.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private ItemDao itemDao;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    //添加商品到购物车
    public List<BuyerCart> addItemToCartList(List<BuyerCart> cartList, Long itemId, Integer num) {
        //1. 根据商品SKU ID查询SKU商品信息
        Item item = itemDao.selectByPrimaryKey(itemId);
        //2. 判断商品是否存在不存在, 抛异常
        if (item == null){
            throw new RuntimeException("此商品不存在!");
        }
        //3. 判断商品状态是否为1已审核, 状态不对抛异常
        if (!"1".equals(item.getStatus())){
            throw new RuntimeException("此商品审核未通过,不予许购买!");
        }
        //4.获取商家ID
        String sellerId = item.getSellerId();
        //5.根据商家ID查询购物车列表中是否存在该商家的购物车
        BuyerCart buyerCart = findBuyerCartBySellerId(cartList,sellerId);
        //6.判断如果购物车列表中不存在该商家的购物车
        if (buyerCart == null) {  //不存在  则需要创建一个给它
            //6.a.1 新建购物车对象
            buyerCart = new BuyerCart();

            //需要对该购物车进行初始化
            //设置新建购物车对象的卖家id
            buyerCart.setSellerId(sellerId);
            //设置新建购物车对象的卖家名称
            buyerCart.setSellerName(item.getSeller());
            //创建购物项集合
            List<OrderItem> orderItemList = new ArrayList<>();
            //创建购物项
            OrderItem orderItem = createOrderItem(item, num);
            //将购物项添加到购物项集合中
            orderItemList.add(orderItem);
            //将购物项保存到购物车中
            buyerCart.setOrderItemList(orderItemList);

            //6.a.2 将新建的购物车对象添加到购物车列表
            cartList.add(buyerCart);
        }else {
            //6.b.1如果购物车列表中存在该商家的购物车 (查询购物车明细列表中是否存在该商品)
            List<OrderItem> orderItemList = buyerCart.getOrderItemList();
            OrderItem orderItem = findOrderItemByItemId(orderItemList,itemId);
            //6.b.2判断购物车明细是否为空
            if (orderItem == null){//不存在该商品
                //6.b.3为空，新增购物车明细
                orderItem = createOrderItem(item,num);
                //将新增的购物项加入到购物项集合中
                orderItemList.add(orderItem);
            }else{
                //6.b.4不为空，在原购物车明细上添加数量，更改金额
                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setTotalFee(orderItem.getPrice().multiply(new BigDecimal(orderItem.getNum())));
                //6.b.5如果购物车明细中数量操作后小于等于0，则移除
                if (orderItem.getNum()<=0){
                    orderItemList.remove(orderItem);
                }
                //6.b.6如果购物车中购物车明细列表为空,则移除
                if (orderItemList.size()<=0){
                    cartList.remove(buyerCart);
                }
            }
        }
        //7. 返回购物车列表对象
        return cartList;
    }


   /* @Override
    //将购物车保存到redis中
    public void setCartListToRedis(String userName, List<BuyerCart> cartList) {
        redisTemplate.boundHashOps(Constants.CATEGORY_LIST_REDIS).put(userName,cartList);
    }

    @Override
    //从redis中获取购物车
    public List<BuyerCart> getCartListFromRedis(String userName) {
        List<BuyerCart> cartList = (List<BuyerCart>)redisTemplate.boundHashOps(Constants.CATEGORY_LIST_REDIS).get(userName);
        if (cartList==null){
            cartList = new ArrayList<>();
        }
        return cartList;
    }*/
   @Override
   public void setCartListToRedis(String userName, List<BuyerCart> cartList) {
       redisTemplate.boundHashOps(Constants.CART_LIST_REDIS).put(userName, cartList);

   }

    @Override
    public List<BuyerCart> getCartListFromRedis(String userName) {
        List<BuyerCart> cartList = (List<BuyerCart>)redisTemplate.boundHashOps(Constants.CART_LIST_REDIS).get(userName);
        if (cartList == null) {
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    @Override
    //合并cookie和redis中的购物车
    public List<BuyerCart> mergeCookieCartListToRedisCartList(List<BuyerCart> cookieCartList, List<BuyerCart> redisCartList) {
        if (cookieCartList!= null){
            //遍历cookie购物车集合
            for (BuyerCart cookieCart : cookieCartList) {
                //遍历cookie购物车中的购物项集合
                for (OrderItem orderItem : cookieCart.getOrderItemList()) {
                    //将cookie中的购物项加入到redis的购物车集合中
                    redisCartList = addItemToCartList( redisCartList,orderItem.getItemId(),orderItem.getNum());
                }
            }
        }
        return redisCartList;
    }


    //定义一个方法  查询此购物车集合中有没有这个卖家的购物车对象, 有则返回, 没有返回null
    private BuyerCart findBuyerCartBySellerId(List<BuyerCart> cartList, String sellerId){
        if (cartList != null){
            for (BuyerCart cart : cartList) {
                if (sellerId.equals(cart.getSellerId())){
                    return cart;
                }
            }
        }
        return null;
    }

    //定义一个方法  创建购物项对象是对数据初始化
    private OrderItem createOrderItem(Item item, Integer num) {
        if(num <= 0){
            throw new RuntimeException("购买数量非法!");
        }
        OrderItem orderItem = new OrderItem();
        orderItem.setNum(num);//购买数量
        orderItem.setGoodsId(item.getGoodsId());//商品id
        orderItem.setItemId(item.getId());//库存id
        orderItem.setPicPath(item.getImage());//示例图片
        orderItem.setPrice(item.getPrice());//商品单价
        orderItem.setSellerId(item.getSellerId());//卖家id
        orderItem.setTitle(item.getTitle());//商品库存标题
        orderItem.setTotalFee(item.getPrice().multiply(new BigDecimal(num)));//总价
        return orderItem;
    }

    //定义一个方法  通过库存id查询购物项集合中是否存在该购物项
    //从当前购物项集合中查询是否存在这个商品, 存在则返回这个购物项对象, 不存在则返回null
    private OrderItem findOrderItemByItemId(List<OrderItem> orderItemList, Long itemId) {
        if (orderItemList!=null){
            for (OrderItem orderItem : orderItemList) {
                if (orderItem.getItemId().equals(itemId)){
                    return orderItem;
                }
            }
        }
        return null;
    }
}

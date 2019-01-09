package com.xyxy.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.xyxy.core.dao.log.PayLogDao;
import com.xyxy.core.dao.order.OrderDao;
import com.xyxy.core.pojo.entity.BuyerCart;
import com.xyxy.core.pojo.log.PayLog;
import com.xyxy.core.pojo.order.Order;
import com.xyxy.core.pojo.order.OrderItem;
import com.xyxy.core.util.Constants;
import com.xyxy.core.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private PayLogDao payLogDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;

    @Override
    public void add(Order order) {
        //1.从订单对象中获取当前登录用户的用户名
        String userId = order.getUserId();
        //2.根据用户名获取购物车集合
        List<BuyerCart> cartList = ( List<BuyerCart>) redisTemplate.boundHashOps(Constants.CART_LIST_REDIS).get(userId);
        List<String> orderIdList = new ArrayList<>();//订单ID列表
        double total_money = 0;//总金额(元)

        //3.遍历购物车集合
        if(cartList!=null){
            for (BuyerCart cart : cartList) {
                //4.根据购物车对象保存订单数据
                long orderId = idWorker.nextId();
                Order tborder=new Order();//新创建订单对象

                //对新建的订单进行初始化
                tborder.setOrderId(orderId);//订单ID
                tborder.setUserId(order.getUserId());//用户名
                tborder.setPaymentType(order.getPaymentType());//支付类型
                tborder.setStatus("1");//状态：未付款
                tborder.setCreateTime(new Date());//订单创建日期
                tborder.setUpdateTime(new Date());//订单更新日期
                tborder.setReceiverAreaName(order.getReceiverAreaName());//地址
                tborder.setReceiverMobile(order.getReceiverMobile());//手机号
                tborder.setReceiver(order.getReceiver());//收货人
                tborder.setSourceType(order.getSourceType());//订单来源
                tborder.setSellerId(cart.getSellerId());//商家ID
                //循环购物车明细
                double money = 0;

                //5.从购物车中获取购物项集合
                List<OrderItem> orderItemList = cart.getOrderItemList();
                //6.遍历购物项集合
               if (orderItemList!=null){
                   for (OrderItem orderItem : orderItemList) {
                        //7.根据购物项对象保存订单的详情数据
                       orderItem.setId(idWorker.nextId());
                       orderItem.setOrderId( orderId  );//订单ID
                       orderItem.setSellerId(cart.getSellerId());
                       money+=orderItem.getTotalFee().doubleValue();
                   }
               }
               tborder.setPayment(new BigDecimal(money));
               //保存订单
                orderDao.insertSelective(tborder);

                orderIdList.add(orderId+"");//添加到订单列表
                total_money+=money;
            }
        }
        //8.计算总价钱保存支付日志数据
        if("1".equals(order.getPaymentType())){//如果是微信支付
            PayLog payLog = new PayLog();
            //生成支付订单号
            String outTradeNo = idWorker.nextId()+"";
            payLog.setOutTradeNo(outTradeNo);//支付订单号
            payLog.setCreateTime(new Date());//创建时间
            //订单号列表，逗号分隔  [aaa, aaa1, aaa2, aaa3, aaa4, aaa5, aaa6]
            String ids=orderIdList.toString().replace("[", "").replace("]", "").replace(" ", "");
            payLog.setOrderList(ids);//订单号列表,逗号隔开
            payLog.setPayType("1");
            payLog.setTotalFee( (long)(total_money*100 ) );//总金额(分)
            payLog.setTradeState("0");//支付状态
            payLog.setUserId(order.getUserId());//用户ID
            payLogDao.insertSelective(payLog);//插入到支付日志表
            //9.使用当前登录用户的用户名作为key,支付日志对象作为value存入到redis中
            redisTemplate.boundHashOps(Constants.PAYLOG_LIST_REDIS).put(order.getUserId(),payLog);
        }
        //10.根据当前用户名删除购物车
        redisTemplate.boundHashOps(Constants.CART_LIST_REDIS).delete(order.getUserId());
    }

    @Override
    //支付成功后 根据用户名开修改支付日志表和订单表的状态
    public void updatePayStatus(String userName) {
        //1.根据登录用户的用户名,获取redis中的支付日志对象
        PayLog payLog = (PayLog) redisTemplate.boundHashOps(Constants.PAYLOG_LIST_REDIS).get(userName);
        //2.根据支付日志对象修改数据库中的支付状态
        payLog.setTradeState("1");
        payLog.setPayTime(new Date());
        payLogDao.updateByPrimaryKeySelective(payLog);

        //3.根据订单id修改订单表的支付状态
        String orderListStr = payLog.getOrderList();
        String[] split = orderListStr.split(",");
        if(split!=null){
            for (String orderId : split) {
                Order order = new Order();
                order.setOrderId(Long.parseLong(orderId));
                order.setStatus("2");
                orderDao.updateByPrimaryKeySelective(order);
            }
        }
        //4.删除redis中这个用户的支付日志对象
        redisTemplate.boundHashOps(Constants.PAYLOG_LIST_REDIS).delete(userName);
    }

    @Override
    public PayLog getPayLogByUserName(String userName) {
        PayLog payLog = (PayLog) redisTemplate.boundHashOps(Constants.PAYLOG_LIST_REDIS).get(userName);
        return payLog;
    }

}

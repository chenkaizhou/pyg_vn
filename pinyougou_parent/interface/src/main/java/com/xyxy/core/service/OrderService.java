package com.xyxy.core.service;

import com.xyxy.core.pojo.log.PayLog;
import com.xyxy.core.pojo.order.Order;
import org.springframework.web.bind.annotation.RequestBody;

public interface OrderService {
    public void add(Order order);
    public void updatePayStatus(String userName);
    public PayLog getPayLogByUserName(String userName);
}

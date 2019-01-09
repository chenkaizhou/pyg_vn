package com.xyxy.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xyxy.core.pojo.entity.Result;
import com.xyxy.core.pojo.order.Order;
import com.xyxy.core.service.OrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    @RequestMapping("/add")
    public Result add(@RequestBody Order order){
        try {
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            order.setUserId(userName);
            orderService.add(order);
            return new Result(true, "保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败!");
        }
    }
}

package com.xyxy.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xyxy.core.pojo.entity.Result;
import com.xyxy.core.pojo.seller.Seller;
import com.xyxy.core.service.SellerService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference
    private SellerService sellerService;

    // 商家注册
    @RequestMapping("/add")
    public Result add(@RequestBody Seller seller){
        try {
            sellerService.add(seller);
            return new Result(true, "注册成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "注册失败!");
        }
    }
}

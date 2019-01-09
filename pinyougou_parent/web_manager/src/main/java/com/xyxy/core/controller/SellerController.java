package com.xyxy.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xyxy.core.pojo.entity.PageResult;
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

    //分页查询  搜索
    @RequestMapping("/search")
    public PageResult findPage(@RequestBody Seller seller,Integer page,Integer rows){
        return sellerService.findPage(seller,page,rows);
    }

    //查询实体  用于回显数据
    @RequestMapping("/findOne")
    public Seller findOne(String id){
        return sellerService.findOne(id);
    }

    //修改商家的状态
    @RequestMapping("/updateStatus")
    public Result updateStatus(String sellerId,String status){
        try {
            sellerService.updateStatus(sellerId,status);
            return new Result(true,"状态修改成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"状态修改失败！");
        }
    }

}

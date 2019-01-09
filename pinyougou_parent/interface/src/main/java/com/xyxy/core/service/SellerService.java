package com.xyxy.core.service;

import com.xyxy.core.pojo.entity.PageResult;
import com.xyxy.core.pojo.seller.Seller;

public interface SellerService {
    //保存注册的商家
    public void add(Seller seller);
    //商家审核  分页查询  搜索
    public PageResult findPage(Seller seller,Integer page,Integer rows);
    //查询实体  用于修改数据的回显
    public Seller findOne(String id);
    //修改商家状态
    public void updateStatus(String sellerId,String status);
}

package com.xyxy.core.service;

import com.xyxy.core.pojo.entity.GoodsEntity;
import com.xyxy.core.pojo.entity.PageResult;
import com.xyxy.core.pojo.good.Goods;

public interface GoodsService {
    //查询所有的商品  分页
    public PageResult findPage( Goods goods,Integer page,Integer rows);

    //增加
    public void add(GoodsEntity goodsEntity);

    //根据id查询商品以及所对应的所有信息
    public GoodsEntity findOne(Long  id);

    //商品的修改
    public void update(GoodsEntity goodsEntity);

    //删除商品
    void delete(Long id);

    //批量提交审核
    void commit(Long[] ids);

    //修改商品的状态  是否上架
    void updateStatus(Long id, String status);
}

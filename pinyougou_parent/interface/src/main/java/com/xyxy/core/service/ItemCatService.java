package com.xyxy.core.service;

import com.xyxy.core.pojo.entity.PageResult;
import com.xyxy.core.pojo.item.ItemCat;

import java.util.List;

public interface ItemCatService {
    //查询所有分类
    public List<ItemCat> findByParentId(Long parentId);
    //查询实体  用于回显
    public ItemCat findOne(Long id);
    //修改
    public void update(ItemCat itemCat);
    //查询所有分类
    public List<ItemCat> findAll();

    //添加
    void add(ItemCat itemCat);

    //删除
    void delete(Long[] ids);

    PageResult findPage( Integer page, Integer rows);
}

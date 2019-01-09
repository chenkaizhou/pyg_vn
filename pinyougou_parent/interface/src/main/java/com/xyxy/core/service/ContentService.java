package com.xyxy.core.service;

import com.xyxy.core.pojo.ad.Content;
import com.xyxy.core.pojo.entity.PageResult;

import java.util.List;

public interface ContentService {
    //分页查询 搜索
    public PageResult findPage(Content content,Integer page,Integer rows);

    //查询实体  回显数据
    public Content findOne(Long id);

    //删除
    public void delete(Long[] ids);

    //修改
    public void update(Content content);

    //添加
    public void add(Content content);

    //根据分类id查询广告
    List<Content> findByCategoryId(Long categoryId);
}

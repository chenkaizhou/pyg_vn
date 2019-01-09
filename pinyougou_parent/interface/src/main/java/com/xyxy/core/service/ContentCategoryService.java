package com.xyxy.core.service;

import com.xyxy.core.pojo.ad.ContentCategory;
import com.xyxy.core.pojo.entity.PageResult;

import java.util.List;

public interface ContentCategoryService {

    //分页查询 搜索
    public PageResult findPage(ContentCategory contentCategory,Integer page, Integer rows);

    //查询实体
    public ContentCategory findOne(Long id);

    //删除
    public void delete(Long[] ids);

    //添加
    public void add(ContentCategory contentCategory);

    //修改
    public void update(ContentCategory contentCategory);

    //查询所有
    public List<ContentCategory> findAll();
}

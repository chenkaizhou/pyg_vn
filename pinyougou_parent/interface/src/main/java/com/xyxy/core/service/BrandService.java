package com.xyxy.core.service;

import com.xyxy.core.pojo.entity.PageResult;
import com.xyxy.core.pojo.good.Brand;

import java.util.List;
import java.util.Map;

public interface BrandService {
    //查询所有的品牌
    public List<Brand> findAll();
    //分页查询(带条件的)  返回一个自定义的结果对象，里面封装了查询结果
    public PageResult findPage(Brand brand,Integer pageNum,Integer pageSize);
    //增加品牌
    public void add(Brand brand);
    //品牌修改
    public void update(Brand brand);
    //修改品牌要进行回显根据id
    public Brand findOne(Long id);
    //批量删除品牌
    public void delete(Long[] ids);
    //模板管理添加其模板时回显品牌
    public List<Map> selectOptionList();
}

package com.xyxy.core.service;

import com.xyxy.core.pojo.entity.PageResult;
import com.xyxy.core.pojo.entity.SpecEntity;
import com.xyxy.core.pojo.specification.Specification;

import java.util.List;
import java.util.Map;

public interface SpecificationService {
    //查询所有
    public List<Specification> findAll();
    //分页查询 带条件
    public PageResult findPage(Specification specification,Integer pageNum , Integer rows);
    //规格添加
    public void add(SpecEntity specEntity);
    //查询实体
    public SpecEntity findOne(Long id);
    //规格修改
    public void update(SpecEntity specEntity);
    //删除规格
    public void dele(Long[] ids);
    //模板管理添加其模板时回显规格
    public List<Map> selectOptionList();
}

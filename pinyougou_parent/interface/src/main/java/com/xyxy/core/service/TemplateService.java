package com.xyxy.core.service;

import com.xyxy.core.pojo.entity.PageResult;
import com.xyxy.core.pojo.template.TypeTemplate;

import java.util.List;
import java.util.Map;

public interface TemplateService {
    //分页查询  带搜索
    public PageResult findPage(TypeTemplate template,Integer pageNum, Integer rows);
    //查询实体  用于修改时回显
    public TypeTemplate findOne(Long id);
    //添加
    public void add(TypeTemplate template);
    //修改
    public void update (TypeTemplate template);
    //删除
    public void detele(Long[] ids);
    //查询所有的模板用于分类管理数据修改回显
    public List<Map> selectOptionList();
    //根据模板id查询 查询规格集合和对应的规格选项集合数据
    public List<Map> findBySpecList(Long id);

    //查询所有的模板
    List<TypeTemplate> findAll();
}

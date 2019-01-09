package com.xyxy.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xyxy.core.pojo.ad.ContentCategory;
import com.xyxy.core.pojo.entity.PageResult;
import com.xyxy.core.service.ContentCategoryService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/contentCategory")
public class ContentCategoryController {
    @Reference
    private ContentCategoryService contentCategoryService;

    //分页查询 搜索
    @RequestMapping("/search")
    public PageResult findPage(@RequestBody ContentCategory contentCategory,Integer page,Integer rows){
        return contentCategoryService.findPage(contentCategory,page,rows);
    }

    //查询所有的分类
    @RequestMapping("/findAll")
    public List<ContentCategory> findAll(){
        return contentCategoryService.findAll();
    }
}

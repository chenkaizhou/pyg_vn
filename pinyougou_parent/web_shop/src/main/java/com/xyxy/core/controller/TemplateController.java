package com.xyxy.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xyxy.core.pojo.entity.PageResult;
import com.xyxy.core.pojo.entity.Result;
import com.xyxy.core.pojo.template.TypeTemplate;
import com.xyxy.core.service.TemplateService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(("/typeTemplate"))
public class TemplateController {

    @Reference
    private TemplateService templateService;


    //查询实体  用于修改是回显
    @RequestMapping("/findOne")
    public TypeTemplate findOne(Long id){
        return templateService.findOne(id);
    }

    //根据模板id。查询规格集合和对应的规格选项集合数据  回显
    @RequestMapping("/findBySpecList")
    public List<Map> findBySpecList(Long id){
        return templateService.findBySpecList(id);
    }
}

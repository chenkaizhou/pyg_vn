package com.xyxy.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xyxy.core.service.TemplateService;
import com.xyxy.core.pojo.entity.PageResult;
import com.xyxy.core.pojo.entity.Result;
import com.xyxy.core.pojo.template.TypeTemplate;
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

    //分页查询  搜索
    @RequestMapping("/search")
    public PageResult findPage(@RequestBody TypeTemplate typeTemplate,Integer page,Integer rows){
        return templateService.findPage(typeTemplate, page, rows);
    }

    //查询实体  用于修改是回显
    @RequestMapping("/findOne")
    public TypeTemplate findOne(Long id){
        return templateService.findOne(id);
    }

    //增加
    @RequestMapping("/add")
    public Result add(@RequestBody TypeTemplate typeTemplate){
        try {
            templateService.add(typeTemplate);
            return new Result(true,"添加成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败！");
        }
    }

    //修改
    @RequestMapping("/update")
    public Result update(@RequestBody TypeTemplate typeTemplate){
        try {
            templateService.update(typeTemplate);
            return new Result(true,"修改成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败！");
        }
    }

    //删除
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            templateService.detele(ids);
            return new Result(true,"删除成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败！");
        }
    }

    //查询所有的模板用于分类管理修改数据回显使用
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return templateService.selectOptionList();
    }

    //查询所有的模板
    @RequestMapping("/findAll")
    public List<TypeTemplate> findAll(){
        return templateService.findAll();
    }
}

package com.xyxy.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xyxy.core.pojo.ad.Content;
import com.xyxy.core.pojo.entity.PageResult;
import com.xyxy.core.pojo.entity.Result;
import com.xyxy.core.service.ContentService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/content")
public class ContenController {

    @Reference
    private ContentService contentService;

    //分页查询
    @RequestMapping("/search")
    public PageResult findPage(@RequestBody Content content,Integer page,Integer rows){
        return contentService.findPage(content,page,rows);
    }

    //数据回显  查询实体
    @RequestMapping("/findOne")
    public Content finfOne(Long id){
        return contentService.findOne(id);
    }

    //修改
    @RequestMapping("/update")
    public Result update(@RequestBody Content content){
        try {
            contentService.update(content);
            return new Result(true,"修改成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败！");
        }
    }

    //添加
    @RequestMapping("/add")
    public Result add(@RequestBody Content content){
        try {
            contentService.add(content);
            return new Result(true,"添加成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败！");
        }
    }

    //批量删除
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            contentService.delete(ids);
            return new Result(true,"删除成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true,"删除失败！");
        }
    }
}

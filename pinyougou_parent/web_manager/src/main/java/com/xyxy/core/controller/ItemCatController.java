package com.xyxy.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xyxy.core.pojo.entity.PageResult;
import com.xyxy.core.pojo.entity.Result;
import com.xyxy.core.pojo.good.Brand;
import com.xyxy.core.pojo.item.ItemCat;
import com.xyxy.core.service.ItemCatService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/itemCat")
public class ItemCatController {

    @Reference
    private ItemCatService itemCatService;

    //查询所有分类数据
    @RequestMapping("/findByParentId")
    public List<ItemCat> findByParentId(Long parentId){
        return itemCatService.findByParentId(parentId);
    }

    //查询实体  数据回显
    @RequestMapping("/findOne")
    public ItemCat findOne(Long id){
        return itemCatService.findOne(id);
    }

    //修改
    @RequestMapping("/update")
    public Result update(@RequestBody ItemCat itemCat){
        try {
            itemCatService.update(itemCat);
            return new Result(true,"修改成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败！");
        }
    }

    //添加
    @RequestMapping("/add")
    public Result add(@RequestBody ItemCat itemCat){
        try {
            itemCatService.add(itemCat);
            return new Result(true,"添加成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败！");
        }
    }

    //删除
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            itemCatService.delete(ids);
            return new Result(true,"删除成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败！");
        }
    }

    //分页查询
    @RequestMapping("/search")
    public PageResult findPage( Integer page, Integer rows){
        return itemCatService.findPage(page,rows);
    }

    //查询所有的分类
    @RequestMapping("/findAll")
    public List<ItemCat> findAll(){
        return itemCatService.findAll();
    }
}

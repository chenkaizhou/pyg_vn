package com.xyxy.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xyxy.core.service.BrandService;
import com.xyxy.core.pojo.entity.PageResult;
import com.xyxy.core.pojo.entity.Result;
import com.xyxy.core.pojo.good.Brand;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {
    @Reference
    private BrandService brandService;

    //查询全部
    @RequestMapping("/findAll")
    public List<Brand> findAll(){
        List<Brand> brandList = brandService.findAll();
        return brandList;
    }

    //分页查询
    @RequestMapping("/search")
    public PageResult findPage(@RequestBody Brand brand ,Integer page,Integer rows){
        return brandService.findPage(brand,page,rows);
    }

    //品牌增加
    @RequestMapping("/add")
    //@RequestBody 将前台传进的json数据转为java数据
    public Result add(@RequestBody Brand brand){
        try {
            brandService.add(brand);
            return new Result(true,"增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"增加失败");

        }
    }

    //品牌修改
    @RequestMapping("/update")
    public Result update(@RequestBody Brand brand){
        try {
            brandService.update(brand);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    //对进行的修改品牌数据回显
    @RequestMapping("/findOne")
    public Brand findOne(Long id){
        return brandService.findOne(id);
    }

    //批量删除品牌
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            brandService.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    //模板管理添加其模板时回显品牌
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }
}

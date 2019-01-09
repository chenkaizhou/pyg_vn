package com.xyxy.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xyxy.core.service.SpecificationService;
import com.xyxy.core.pojo.entity.PageResult;
import com.xyxy.core.pojo.entity.Result;
import com.xyxy.core.pojo.entity.SpecEntity;
import com.xyxy.core.pojo.specification.Specification;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
//规格管理

@RestController
@RequestMapping("/specification")
public class SpecificationController {
    @Reference
    private SpecificationService specService;

    //查询所有
    @RequestMapping("/findAll")
    public List<Specification> findAll(){
        return specService.findAll();
    }

    //分页查询 带条件
    @RequestMapping("/search")
    public PageResult findPage(@RequestBody Specification specification,Integer page,Integer rows){
        return specService.findPage(specification,page,rows);
    }

    //规格添加
    @RequestMapping("/add")
    public Result add(@RequestBody SpecEntity specEntity){
        try {
            System.out.println(specEntity);
            specService.add(specEntity);
            return new Result(true,"保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"保存失败");
        }
    }

    //查询实体  根据id查询规格和对应的规格选项  用于修改数据回显
    @RequestMapping("/findOne")
    public SpecEntity findOne(Long id){
        SpecEntity one = specService.findOne(id);
        return one;
    }

    //修改规格名称和规格选项
    @RequestMapping("/update")
    public Result update(@RequestBody SpecEntity specEntity){
        try {
            specService.update(specEntity);
            return new Result(true,"修改成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true,"修改失败!");
        }
    }

    //删除规格名称和规格选项
    @RequestMapping("/delete")
    public Result dele(Long[] ids){
        try {
            specService.dele(ids);
            return new Result(true,"删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true,"删除失败!");
        }
    }

    //模板管理添加其模板时回显规格
    @RequestMapping("selectOptionList")
    public List<Map> selectOptionList(){
        return specService.selectOptionList();
    }
}

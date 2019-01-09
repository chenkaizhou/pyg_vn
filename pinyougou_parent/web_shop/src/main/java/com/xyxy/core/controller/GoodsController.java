package com.xyxy.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xyxy.core.pojo.entity.GoodsEntity;
import com.xyxy.core.pojo.entity.PageResult;
import com.xyxy.core.pojo.entity.Result;
import com.xyxy.core.pojo.good.Goods;
import com.xyxy.core.service.GoodsService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Reference
    private GoodsService goodsService;

    //新增商品
    @RequestMapping("/add")
    public Result add(@RequestBody GoodsEntity goodsEntity){
        try {
            //获取登录用户名名
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            //设置这个商品添加的用户名，也就是卖家id
            goodsEntity.getGoods().setSellerId(userName);

            goodsService.add(goodsEntity);
            return new Result(true,"添加成功！");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败！");
        }
    }

    //查询所有商品
    @RequestMapping("/search")
    public PageResult findPage(@RequestBody Goods goods,Integer page,Integer rows){
        //获取当前登录用户名
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(userName);
        return goodsService.findPage(goods,page,rows);
    }

    //修改商品数据回显
    @RequestMapping("/findOne")
    public GoodsEntity findOne(Long id){
        return goodsService.findOne(id);
    }

    //修改
    @RequestMapping("/update")
    public Result update(@RequestBody GoodsEntity goodsEntity){
        try {
            //获取当前登录用户名
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            //获取这个商品的所有者
            String sellerId = goodsEntity.getGoods().getSellerId();
            if (!userName.equals(sellerId)){
                //用户名不相同  没有修改的权限
                return new Result(false,"您没有修改此商品的权限！");
            }
            goodsService.update(goodsEntity);
            return new Result(true,"修改成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败！");
        }
    }

    //删除  删除不是真正的删除表中数据  只是改变数据某个字段的状态
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            if(ids.length>0){
                for (Long id : ids) {
                    goodsService.delete(id);
                }
            }
            return new Result(true,"删除成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败！");
        }
    }

    //提交审核 批量
    @RequestMapping("/commit")
    public Result commit(Long[] ids){
        try {
            goodsService.commit(ids);
            return new Result(true,"提交成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"提交失败！");
        }
    }
}

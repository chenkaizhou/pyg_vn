package com.xyxy.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xyxy.core.pojo.entity.GoodsEntity;
import com.xyxy.core.pojo.entity.PageResult;
import com.xyxy.core.pojo.entity.Result;
import com.xyxy.core.pojo.good.Goods;
import com.xyxy.core.service.CmsService;
import com.xyxy.core.service.GoodsService;
import com.xyxy.core.service.SolrManagerService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Reference
    private GoodsService goodsService;

//    @Reference
//    private SolrManagerService solrManagerService;
//    @Reference
//    private CmsService cmsService;

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

    //删除商品
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            if(ids!=null){
                for (Long id : ids) {
                    //根据id到数据库中删除
                    goodsService.delete(id);
                    //根据商品id到solr索引库中删除对应的数据
                   // solrManagerService.deleteItemFromSolr(id);
                }
            }
            return new Result(true,"删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败!");
        }
    }

    //修改商品状态  生成静态页html页面
    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids,String status){
        try {
            if (ids.length>0){
                for (Long id : ids) {
                    //到数据库中根据商品id改变商品的上架状态
                    goodsService.updateStatus(id,status);
                   /* //对于审核通过的商品
                    if("1".equals(status)){
                        //将根据商品id获取库存数据,放入solr索引库供搜索使用
                        solrManagerService.saveItemToSolr(id);
                        //根据商品id获取商品的详情数据,并且根据数据和模板生成商品的详情页面
                        Map<String, Object> goodsData = cmsService.findGoodsData(id);
                        try {
                            cmsService.createStaticPage(id,goodsData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }*/
                }
            }
            return new Result(true,"状态修改成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"状态修改失败!");
        }
    }

}

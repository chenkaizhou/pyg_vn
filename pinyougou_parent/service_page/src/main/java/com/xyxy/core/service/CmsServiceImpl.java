package com.xyxy.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.xyxy.core.dao.good.GoodsDao;
import com.xyxy.core.dao.good.GoodsDescDao;
import com.xyxy.core.dao.item.ItemCatDao;
import com.xyxy.core.dao.item.ItemDao;
import com.xyxy.core.pojo.good.Goods;
import com.xyxy.core.pojo.good.GoodsDesc;
import com.xyxy.core.pojo.item.Item;
import com.xyxy.core.pojo.item.ItemCat;
import com.xyxy.core.pojo.item.ItemQuery;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;


import javax.servlet.ServletContext;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CmsServiceImpl implements CmsService, ServletContextAware {
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private ItemCatDao itemCatDao;
    @Autowired
    private GoodsDescDao goodsDescDao;
    @Autowired
    private FreeMarkerConfigurer freemarkerConfig;

    //需要获取绝对路径需要使用servletContext对象  通过实现ServletContextAware接口中的方法获取
    private ServletContext servletContext;


    @Override
    public void createStaticPage(Long goodsId, Map<String, Object> rootMap) throws Exception {
        //1. 获取模板的初始化对象
        Configuration configuration = freemarkerConfig.getConfiguration();
        //2. 获取模板对象
        Template template = configuration.getTemplate("item.ftl");

        //3. 创建输出流, 指定生成静态页面的位置和名称
        String path = goodsId + ".html";
        String realPath = getRealPath(path);

        Writer out = new OutputStreamWriter(new FileOutputStream(new File(realPath)), "utf-8");
        //4. 生成
        template.process(rootMap, out);
        //5.关闭流
        out.close();

    }

    @Override
    //根据商品id获取商品的相关信息
    public Map<String, Object> findGoodsData(Long goodsId) {
        //创建一个Map对象来村存储商品 商品详情 商品库存
        Map<String,Object> resultMap = new HashMap<>();
        //根据id获取商品数据
        Goods goods = goodsDao.selectByPrimaryKey(goodsId);
        //获取商品详情数据
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(goodsId);
        //获取库存集合数据
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(goodsId);
        List<Item> itemList = itemDao.selectByExample(query);
        //获取商品对应的分类数据
        if(goods!=null){
            ItemCat itemCat1 = itemCatDao.selectByPrimaryKey(goods.getCategory1Id());
            ItemCat itemCat2 = itemCatDao.selectByPrimaryKey(goods.getCategory2Id());
            ItemCat itemCat3 = itemCatDao.selectByPrimaryKey(goods.getCategory3Id());
            resultMap.put("itemCat1",itemCat1.getName());
            resultMap.put("itemCat2",itemCat2.getName());
            resultMap.put("itemCat3",itemCat3.getName());
        }
        //将商品所有数据封装成map返回
        resultMap.put("goods",goods);
        resultMap.put("itemList",itemList);
        resultMap.put("goodsDesc",goodsDesc);
        return resultMap;
    }

    @Override
    //获取servletContext对象  通过实现接口来获取
    public void setServletContext(ServletContext servletContext) {
        this.servletContext=servletContext;
    }

    //自定义一个方法  将相对路径转换成绝对路径 生成静态页面存储的路径
    private String getRealPath(String path){
        String realPath = servletContext.getRealPath(path);
        return realPath;
    }
}

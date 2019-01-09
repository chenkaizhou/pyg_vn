package com.xyxy.core.pojo.entity;

import com.xyxy.core.pojo.good.Goods;
import com.xyxy.core.pojo.good.GoodsDesc;
import com.xyxy.core.pojo.item.Item;
import com.xyxy.core.pojo.item.ItemCat;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class GoodsEntity implements Serializable {
    //商品SPU
    private Goods  goods;
    //商品扩展
    private GoodsDesc goodsDesc;
    //商品SKU列表
    private List<Item> itemList;

    //商品对应的分类
    private Map<String,String> itemCatMap;

    //品牌名称
    private String brandName;

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public Map<String, String> getItemCatMap() {
        return itemCatMap;
    }

    public void setItemCatMap(Map<String, String> itemCatMap) {
        this.itemCatMap = itemCatMap;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public GoodsDesc getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(GoodsDesc goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }


}

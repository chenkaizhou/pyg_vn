package com.xyxy.core.service;

public interface SolrManagerService {
    //根据商品id将商品保存到索引库中
    public void saveItemToSolr(Long id);
    //根据商品id将商品从索引库中删除
    public void deleteItemFromSolr(Long id);
}

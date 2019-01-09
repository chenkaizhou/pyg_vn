package com.xyxy.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.xyxy.core.dao.item.ItemDao;
import com.xyxy.core.pojo.item.Item;
import com.xyxy.core.pojo.item.ItemQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;

import java.util.List;
import java.util.Map;

@Service
public class SolrManagerServiceImpl implements SolrManagerService {
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private SolrTemplate solrTemplate;


    @Override
    //根据id将商品保存到索引库中
    public void saveItemToSolr(Long id) {
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<Item> items = itemDao.selectByExample(query);
        if (items.size()>0){
            for (Item item : items) {
                //获取规格json格式的字符串
                String specJsonStr = item.getSpec();
                Map map = JSON.parseObject(specJsonStr, Map.class);
                item.setSpecMap(map);
            }
            //保存到索引库中
            solrTemplate.saveBeans(items);
            //提交
            solrTemplate.commit();
        }
    }

    @Override
    //根据id将商品从索引库中删除
    public void deleteItemFromSolr(Long id) {
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").is(id);
        query.addCriteria(criteria);
        //删除
        solrTemplate.delete(query);
        //提交
        solrTemplate.commit();
    }
}

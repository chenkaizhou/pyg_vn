package com.xyxy.core.util;

import com.alibaba.fastjson.JSON;
import com.xyxy.core.dao.item.ItemDao;
import com.xyxy.core.pojo.item.Item;
import com.xyxy.core.pojo.item.ItemQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DataImportToSolr {
    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private ItemDao itemDao;

    public void importItemDataToSolr(){
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andStatusEqualTo("1");
        List<Item> items = itemDao.selectByExample(query);
        if(items!=null){
            for (Item item : items) {
                //获取规格json格式字符串
                String itemSpec = item.getSpec();
                Map map = JSON.parseObject(itemSpec, Map.class);
                item.setSpecMap(map);
            }
            //保存
            solrTemplate.saveBeans(items);
            //提交
            solrTemplate.commit();
        }
    }

    public static void main(String[] args) {
        ApplicationContext contexnt = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        DataImportToSolr bean = (DataImportToSolr)contexnt.getBean("dataImportToSolr");
        bean.importItemDataToSolr();
    }

}

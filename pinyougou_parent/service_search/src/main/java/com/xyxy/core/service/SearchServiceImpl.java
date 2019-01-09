package com.xyxy.core.service;



import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.xyxy.core.pojo.item.Item;
import com.xyxy.core.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.*;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map paramMap) {
        //1.根据查询参数，到solr中分页，高亮，过滤，排序查询
        Map<String, Object> resultMap = highLightSearch(paramMap);

        //2.根据查询参数，到solr中获取对应的分类参数结果集，由于分类重复，所以需要进行分组去重
        List<String> categoryList = findGroupCategoryList(paramMap);
        if (categoryList.size()>0){
            resultMap.put("categoryList", categoryList);
        }else {
            ArrayList<String> list = new ArrayList<>();
            list.add("<em style=\"color:red\">没有找到与</em>"+paramMap.get("keywords")+"<em style=\"color:red\">相关数据！</em>");
            resultMap.put("categoryList", list);
        }

        //3.判断paramMap传入的参数中是否有分类名称
        String category = String.valueOf(paramMap.get("category"));
        if (category != null && !"".equals(category)) {
            //有分类参数，则根据分类参数查询对应的品牌集合和规格集合
            Map specListAndBrandList = findSpecListAndBrandList(category);
            resultMap.putAll(specListAndBrandList);
        } else {
            //没有分类参数，则默认第一个分类查询对应的品牌集合和规格集合
            if(categoryList.size()>0){
                Map specListAndBrandList = findSpecListAndBrandList(categoryList.get(0));
                resultMap.putAll(specListAndBrandList);
            }
        }

        return resultMap;
    }

    //根据关键字，分页，高亮，过滤，排序查询，并且将查询结果返回
    private Map<String, Object> highLightSearch(Map paramMap) {
        /**
         * 获取查询条件
         */
        //获取查询关键字
        String keywords = String.valueOf(paramMap.get("keywords"));
        if(keywords.length()>0){
            keywords=keywords.replaceAll(" ","");
        }
        //当前页
        Integer pageNo = Integer.parseInt(String.valueOf(paramMap.get("pageNo")));
        //每页查询多少条数据
        Integer pageSize = Integer.parseInt(String.valueOf(paramMap.get("pageSize")));
        //获取页面点击的分类过滤条件
        String category = String.valueOf(paramMap.get("category"));
        //获取页面点击的品牌过滤条件
        String brand = String.valueOf(paramMap.get("brand"));
        //获取页面点击的价格区间过滤条件
        String price = String.valueOf(paramMap.get("price"));
        //获取页面点击的规格过滤条件
        String spec = String.valueOf(paramMap.get("spec"));
        //获取页面传入的排序的域
        String sortField = String.valueOf(paramMap.get("sortField"));
        //获取页面传入的排序方式
        String sortType= String.valueOf(paramMap.get("sort"));

        /**
         * 封装查询对象
         */
        //创建查询对象
        HighlightQuery query = new SimpleHighlightQuery();
        //创建查询条件对象
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        //将查询条件放入查询对象中
        query.addCriteria(criteria);

        //算从第几条开始查询
        if (pageNo == null || pageNo <= 0) {
            pageNo = 1;
        }
        Integer start = (pageNo - 1) * pageSize;
        //设置从第几条开始查询
        query.setOffset(start);
        //设置每页查询多少条数据
        query.setRows(pageSize);

        //创建高亮选项对象
        HighlightOptions highlightOptions = new HighlightOptions();
        //设置哪个域需要高显示
        highlightOptions.addField("item_title");
        //设置高亮前缀
        highlightOptions.setSimplePrefix("<em style=\"color:red\">");
        //设置高亮后缀
        highlightOptions.setSimplePostfix("</em>");
        //将高亮选项加入到查询对象中
        query.setHighlightOptions(highlightOptions);

        /**
         * 过滤查询
         */
        //根据分类过滤条件查询
        if (category != null && !"".equals(category)) {
            //创建过滤查询对象
            FilterQuery filterQuery = new SimpleFilterQuery();
            //创建条件对象
            Criteria item_category = new Criteria("item_category").is(category);
            //将条件对象放入到顾虑对象中
            filterQuery.addCriteria(item_category);
            //过滤对象放入到查询对象中
            query.addFilterQuery(filterQuery);
        }

        //根据品牌过滤查询
        if (brand != null && !"".equals(brand)) {
            //创建过滤查询对象
            FilterQuery filterQuery = new SimpleFilterQuery();
            //创建条件对象
            Criteria brand_category = new Criteria("item_brand").is(brand);
            //将条件对象放入到顾虑对象中
            filterQuery.addCriteria(brand_category);
            //过滤对象放入到查询对象中
            query.addFilterQuery(filterQuery);
        }

        //根据规格过滤
        if(spec!=null && !"".equals(spec)){
            Map<String,String> specMap = JSON.parseObject(spec, Map.class);
            if(specMap!=null && specMap.size()>0){
                Set<Map.Entry<String, String>> entries = specMap.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    //创建过滤查询对象
                    FilterQuery filterQuery = new SimpleFilterQuery();
                    //创建条件对象
                    Criteria specCriteria = new Criteria("item_spec_"+entry.getKey()).is(entry.getValue());
                    //将条件对象放入到顾虑对象中
                    filterQuery.addCriteria(specCriteria);
                    //过滤对象放入到查询对象中
                    query.addFilterQuery(filterQuery);
                }
            }
        }

        //根据价格过滤
        if (price != null && !"".equals(price)) {
            //切分价格，这个数组中有最小值 和  最大值
            String[] split = price.split("-");
            if (split != null && split.length == 2) {
                //说明大于等于最小值，如果第一个最小值是0  不能进入这里
                if (!"0".equals(split[0])) {
                    //创建过滤查询对象
                    FilterQuery filterQuery = new SimpleFilterQuery();
                    //创建条件对象
                    Criteria item_price = new Criteria("item_price").greaterThanEqual(split[0]);
                    //将条件对象放入到顾虑对象中
                    filterQuery.addCriteria(item_price);
                    //过滤对象放入到查询对象中
                    query.addFilterQuery(filterQuery);
                }
                //说明小于等于最大值，如果最后的元素是*  不能进入这里
                if (!"*".equals(split[1])) {
                    //创建过滤查询对象
                    FilterQuery filterQuery = new SimpleFilterQuery();
                    //创建条件对象
                    Criteria item_price = new Criteria("item_price").lessThanEqual(split[1]);
                    //将条件对象放入到顾虑对象中
                    filterQuery.addCriteria(item_price);
                    //过滤对象放入到查询对象中
                    query.addFilterQuery(filterQuery);
                }
            }
        }
        /**
         * 添加排序条件
         */
       if (sortField.length()>0 && sortType.length()>0){
           //升序排序
           if("ASC".equals(sortType)){
               //创建排序对象
               Sort sort = new Sort(Sort.Direction.ASC,"item_"+sortField);
               //将排序对象放入到查询对象中
               query.addSort(sort);
           }
           //降序排序
           if("DESC".equals(sortType)){
               //创建排序对象
               Sort sort = new Sort(Sort.Direction.DESC,"item_"+sortField);
               //将排序对象放入到查询对象中
               query.addSort(sort);
           }
       }


        /**
         * 查询并返回结果
         */
        HighlightPage<Item> items = solrTemplate.queryForHighlightPage(query, Item.class);

        //获取带高亮的集合
        List<HighlightEntry<Item>> highlighted = items.getHighlighted();

        List<Item> itemList = new ArrayList<>();
        //遍历高亮集合
        for (HighlightEntry<Item> itemHighlightEntry : highlighted) {
            //获取到不带高亮的实体对象
            Item item = itemHighlightEntry.getEntity();
            List<HighlightEntry.Highlight> highlights = itemHighlightEntry.getHighlights();
            if (highlights != null && highlights.size() > 0) {
                //获取到高亮标题集合
                HighlightEntry.Highlight highlight = highlights.get(0);
                List<String> highlightTitle = highlight.getSnipplets();
                if (highlightTitle != null && highlightTitle.size() > 0) {
                    //终于获取到高亮的标题
                    String title = highlightTitle.get(0);
                    item.setTitle(title);
                }
            }

            itemList.add(item);
        }

        Map<String, Object> resultMap = new HashMap<>();
        //查询到的结果集
        resultMap.put("rows", itemList);
        //查询到的总页数
        resultMap.put("totalPages", items.getTotalPages());
        //查询到的总条数
        resultMap.put("total", items.getTotalElements());

        return resultMap;
    }

    //根据查询参数，到solr中获取对应的分类参数结果集，由于分类重复，所以需要进行分组去重
    private List<String> findGroupCategoryList(Map paramMap) {
        List<String> resultList = new ArrayList<>();
        //获取查询的关键字
        String keywords = String.valueOf(paramMap.get("keywords"));
        if(keywords!=null){
            keywords=keywords.replaceAll(" ","");
        }
        //创建查询对象
        Query query = new SimpleQuery();
        //创建查询条件对象
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        //将查询条件放入到查询对象中
        query.addCriteria(criteria);

        //创建分组对象
        GroupOptions groupOptions = new GroupOptions();
        //设置根据分类域进行分组
        groupOptions.addGroupByField("item_category");
        //将分组对象放入到查询对象中
        query.setGroupOptions(groupOptions);

        //分组查询分类集合
        GroupPage<Item> items = solrTemplate.queryForGroupPage(query, Item.class);
        //获取分类结果集中分类域的集合
        GroupResult<Item> item_category = items.getGroupResult("item_category");
        //获取分类域的实体集合
        Page<GroupEntry<Item>> groupEntries = item_category.getGroupEntries();
        //遍历实体集合得到实体对象
        for (GroupEntry<Item> groupEntry : groupEntries) {
            //获取分类值
            String groupCategory = groupEntry.getGroupValue();
            resultList.add(groupCategory);
        }
        return resultList;
    }

    //根据分类名称查询对应的品牌集合和规格集合
    private Map findSpecListAndBrandList(String categoryName){
        //1.根据分类名称到redis中查询对应的模板id
        Long templateId = (Long) redisTemplate.boundHashOps(Constants.CATEGORY_LIST_REDIS).get(categoryName);
        //2.根据模板id到redis中获取对应品牌集合
        List<Map> brandList =(List<Map>) redisTemplate.boundHashOps(Constants.BRAND_LIST_REDIS).get(templateId);
        //3.根据模板id到redis中获取对应的规格集合
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps(Constants.SPEC_LIST_REDIS).get(templateId);
        //4.将品牌集合和规格集合数据封装到Map中返回
        Map resultMap = new HashMap();
        resultMap.put("brandList",brandList);
        resultMap.put("specList",specList);
        return resultMap;
    }
}



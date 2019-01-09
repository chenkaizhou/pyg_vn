package com.xyxy.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xyxy.core.dao.item.ItemCatDao;
import com.xyxy.core.pojo.entity.PageResult;
import com.xyxy.core.pojo.good.Brand;
import com.xyxy.core.pojo.good.BrandQuery;
import com.xyxy.core.pojo.item.ItemCat;
import com.xyxy.core.pojo.item.ItemCatQuery;
import com.xyxy.core.pojo.item.ItemQuery;
import com.xyxy.core.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

@Service
public class ItemCatServiceImpl implements ItemCatService {
    @Autowired
    private ItemCatDao itemCatDao;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    //查询全部分类
    public List<ItemCat> findByParentId(Long parentId) {
        findAllImportToRedis();
        ItemCatQuery query = new ItemCatQuery();
        ItemCatQuery.Criteria criteria = query.createCriteria();
        if(parentId != null && !"".equals(parentId)){
            criteria.andParentIdEqualTo(parentId);
        }
        return itemCatDao.selectByExample(query);
    }

    @Override
    //查询实体  用于回显
    public ItemCat findOne(Long id) {
        return itemCatDao.selectByPrimaryKey(id);
    }

    @Override
    //修改
    public void update(ItemCat itemCat) {
        itemCatDao.updateByPrimaryKeySelective(itemCat);
        findAllImportToRedis();
    }

    @Override
    public List<ItemCat> findAll() {
        List<ItemCat> itemCatList = itemCatDao.selectByExample(null);
        return itemCatList;
    }

    @Override
    //添加
    public void add(ItemCat itemCat) {
        itemCatDao.insertSelective(itemCat);
        findAllImportToRedis();
    }

    //删除
    public void delete(Long[] ids){
        if(ids!=null){
            for (Long id : ids) {
                ItemCatQuery query = new ItemCatQuery();
                ItemCatQuery.Criteria criteria = query.createCriteria();
                criteria.andParentIdEqualTo(id);
                List<ItemCat> itemCats = itemCatDao.selectByExample(query);
                if(itemCats.size()==0){
                    itemCatDao.deleteByPrimaryKey(id);
                }
            }
            findAllImportToRedis();
        }
    }

    @Override
    public PageResult findPage( Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        //查询数据  加入条件进行查询
        Page<ItemCat> itemCats = (Page<ItemCat>)itemCatDao.selectByExample(null);
        return new PageResult(itemCats.getTotal(),itemCats.getResult());
    }

    //定义一个方法将分类数据缓存到redis中  key为分类名称  value为id
    private void findAllImportToRedis(){
        List<ItemCat> itemCatList = itemCatDao.selectByExample(null);
        for (ItemCat itemCat : itemCatList) {
            redisTemplate.boundHashOps(Constants.CATEGORY_LIST_REDIS).put(itemCat.getName(),itemCat.getTypeId());
        }
    }
}

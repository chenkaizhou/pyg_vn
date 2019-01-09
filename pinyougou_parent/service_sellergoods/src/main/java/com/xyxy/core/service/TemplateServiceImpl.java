package com.xyxy.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xyxy.core.dao.specification.SpecificationOptionDao;
import com.xyxy.core.dao.template.TypeTemplateDao;
import com.xyxy.core.pojo.entity.PageResult;
import com.xyxy.core.pojo.specification.SpecificationOption;
import com.xyxy.core.pojo.specification.SpecificationOptionQuery;
import com.xyxy.core.pojo.specification.SpecificationQuery;
import com.xyxy.core.pojo.template.TypeTemplate;
import com.xyxy.core.pojo.template.TypeTemplateQuery;
import com.xyxy.core.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TemplateServiceImpl implements TemplateService {
    @Autowired
    private TypeTemplateDao templateDao;
    @Autowired
    private SpecificationOptionDao optionDao;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PageResult findPage(TypeTemplate template, Integer pageNum, Integer rows) {
        findAllImportToRedis();
        PageHelper.startPage(pageNum,rows);
        TypeTemplateQuery query = new TypeTemplateQuery();
        TypeTemplateQuery.Criteria criteria = query.createCriteria();
        if (template!=null){
            //查询窗口的  根据名字
            if (template.getName()!=null && !"".equals(template.getName())){
                criteria.andNameLike("%"+template.getName()+"%");
            }
        }
        Page<TypeTemplate> page =( Page<TypeTemplate>) templateDao.selectByExample(query);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    //查询实体  回显使用
    public TypeTemplate findOne(Long id) {
        if(id!=null){
            return templateDao.selectByPrimaryKey(id);
        }
        return null;
    }

    @Override
    //添加
    public void add(TypeTemplate template) {
        templateDao.insertSelective(template);
        findAllImportToRedis();
    }

    @Override
    //修改
    public void update(TypeTemplate template) {
        templateDao.updateByPrimaryKeySelective(template);
        findAllImportToRedis();
    }

    @Override
    //删除
    public void detele(Long[] ids) {
        if(ids!=null){
            for (Long id : ids) {
                templateDao.deleteByPrimaryKey(id);
            }
            findAllImportToRedis();
        }
    }

    @Override
    //查询所有模板用于分类管理修改数据回显
    public List<Map> selectOptionList() {
        return templateDao.selectOptionList();
    }

    @Override
    //根据模板id查询规格选项表数据 查询规格集合和对应的规格选项集合数据
    public List<Map> findBySpecList(Long id) {
        //根据模板id查询模板对象
        TypeTemplate typeTemplate = templateDao.selectByPrimaryKey(id);
        //从模板对象中获取规格集合数据，获取到的是json格式的字符串   数组形式
        String specIds = typeTemplate.getSpecIds();
        //将json字符串解析为java中的list集合对象
        List<Map> maps = JSON.parseArray(specIds, Map.class);
        //遍历
        if(maps.size()>0){
            for (Map map : maps) {
                //遍历过程中根据规格id查询对应的规格选项集合数据
                //object类型的数据先转为String类型  在转为Long型
                Long specId = Long.parseLong(String.valueOf(map.get("id")));
                SpecificationOptionQuery query = new SpecificationOptionQuery();
                SpecificationOptionQuery.Criteria criteria = query.createCriteria();
                criteria.andSpecIdEqualTo(specId);
                List<SpecificationOption> optionList = optionDao.selectByExample(query);

                //将规格选项集合数据封装到要来的map中
                map.put("options",optionList);
            }
        }
        return maps;
    }

    @Override
    public List<TypeTemplate> findAll() {
        return templateDao.selectByExample(null);
    }

    //将品牌和规格选项集合缓存到redis中  方法
    private void findAllImportToRedis(){
        //redis中缓存模板所有数据
        List<TypeTemplate> templateAll = templateDao.selectByExample(null);
        for (TypeTemplate typeTemplate : templateAll) {
            //找到品牌
            String brandIdsJsonStr = typeTemplate.getBrandIds();
            //将json转换为集合
            List<Map> brandList = JSON.parseArray(brandIdsJsonStr, Map.class);
            //模板id作为key，品牌集合作为value缓存到redis中
            redisTemplate.boundHashOps(Constants.BRAND_LIST_REDIS).put(typeTemplate.getId(),brandList);
            //模板id作为key，规格集合作为value缓存到redis中
            List<Map> specList = findBySpecList(typeTemplate.getId());
            redisTemplate.boundHashOps(Constants.SPEC_LIST_REDIS).put(typeTemplate.getId(),specList);
        }
    }
}

package com.xyxy.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xyxy.core.dao.ad.ContentCategoryDao;
import com.xyxy.core.pojo.ad.ContentCategory;
import com.xyxy.core.pojo.ad.ContentCategoryQuery;
import com.xyxy.core.pojo.entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ContentCategoryServiceImpl implements ContentCategoryService {

    @Autowired
    private ContentCategoryDao contentCategoryDao;

    @Override
    public PageResult findPage(ContentCategory contentCategory, Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        ContentCategoryQuery query = new ContentCategoryQuery();
        ContentCategoryQuery.Criteria criteria = query.createCriteria();
        if(contentCategory!=null){
            if (contentCategory.getName()!=null && !"".equals(contentCategory.getName())){
                criteria.andNameLike("%"+contentCategory.getName()+"%");
            }
        }
        Page<ContentCategory> categories = ( Page<ContentCategory>) contentCategoryDao.selectByExample(query);
        return new PageResult(categories.getTotal(),categories.getResult());
    }

    @Override
    public ContentCategory findOne(Long id) {
        return null;
    }

    @Override
    public void delete(Long[] ids) {

    }

    @Override
    public void add(ContentCategory contentCategory) {

    }

    @Override
    public void update(ContentCategory contentCategory) {

    }

    @Override
    //查询所有
    public List<ContentCategory> findAll() {
        List<ContentCategory> contentCategoryList = contentCategoryDao.selectByExample(null);
        return contentCategoryList;
    }
}

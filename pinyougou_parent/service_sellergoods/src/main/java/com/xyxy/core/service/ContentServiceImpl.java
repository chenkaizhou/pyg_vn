package com.xyxy.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xyxy.core.dao.ad.ContentDao;
import com.xyxy.core.pojo.ad.Content;
import com.xyxy.core.pojo.ad.ContentQuery;
import com.xyxy.core.pojo.entity.PageResult;
import com.xyxy.core.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ContentServiceImpl implements ContentService {
    @Autowired
    private ContentDao contentDao;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    //分页查询  搜索
    public PageResult findPage(Content content,Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        ContentQuery query = new ContentQuery();
        ContentQuery.Criteria criteria = query.createCriteria();
        if(content!=null){
            if(content.getTitle()!=null && !"".equals(content.getTitle())){
                criteria.andTitleLike("%"+content.getTitle()+"%");
            }
        }
        Page<Content> contents = (Page<Content>) contentDao.selectByExample(query);
        return new PageResult(contents.getTotal(),contents.getResult());
    }

    @Override
    //查询实体  回显数据
    public Content findOne(Long id) {
        Content content = contentDao.selectByPrimaryKey(id);
        return content;
    }

    @Override
    //删除
    public void delete(Long[] ids) {
        if(ids!=null){
            for (Long id : ids) {
                //根据id查询数据库中广告对象
                Content content = contentDao.selectByPrimaryKey(id);
                //根据广告对象中的分类id删除redis中对应的广告集合数据
                redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(content.getCategoryId());
                //根据id删除数据库中的广告数据
                contentDao.deleteByPrimaryKey(id);
            }
        }
    }

    @Override
    //修改
    public void update(Content content) {
        //根据广告id到数据库中查询原来的广告对象   获取原来数据的分类id redis根据id删除原来的数据
        Content oldContent = contentDao.selectByPrimaryKey(content.getId());
        //根据原来的广告对象中的分类id删除redis对应的广告集合数据
        redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(oldContent.getCategoryId());
        //根据传入的最新的广告对象中的分类id删除redis中对应的广告集合数据
        redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(content.getCategoryId());
        //将新的数据添加到数据库中
        contentDao.updateByPrimaryKey(content);

    }

    @Override
    //添加
    public void add(Content content) {
        //将新广告添加到数据库中
       contentDao.insertSelective(content);
       //根据分类id到redis中删除对应分类的广告集合数据
        redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(content.getCategoryId());
    }

    @Override
    public List<Content> findByCategoryId(Long categoryId) {
        //首先根据分类id到redis中获取数据
        List<Content> contentList = ( List<Content>)redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).get(categoryId);
        //如果redis中没有数据则到数据库中获取数据
        if(contentList == null){
            ContentQuery query = new ContentQuery();
            ContentQuery.Criteria criteria = query.createCriteria();
            criteria.andCategoryIdEqualTo(categoryId);
            contentList = contentDao.selectByExample(query);
            redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).put(categoryId,contentList);
        }
       ;
        return contentList;
    }
}

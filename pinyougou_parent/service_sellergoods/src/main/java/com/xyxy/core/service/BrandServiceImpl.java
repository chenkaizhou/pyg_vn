package com.xyxy.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xyxy.core.dao.good.BrandDao;
import com.xyxy.core.pojo.entity.PageResult;
import com.xyxy.core.pojo.good.Brand;
import com.xyxy.core.pojo.good.BrandQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandDao brandDao;
    @Override
    //查询所有
    public List<Brand> findAll() {
        List<Brand> brands = brandDao.selectByExample(null);
        return brands;
    }

    @Override
    //分页查询 带条件的
    public PageResult findPage(Brand brand, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        //获取数据的时候带上条件
        //创建sql条件对象
        BrandQuery query = new BrandQuery();
        BrandQuery.Criteria criteria = query.createCriteria();
        //判断条件是否为空 为空就不需要增加条件
        if (brand!=null){
            if (brand.getName()!=null && !"".equals(brand.getName())){
                criteria.andNameLike("%"+brand.getName()+"%");
            }
            if (brand.getFirstChar()!=null && !"".equals(brand.getFirstChar())){
                criteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
            }
        }
        //查询数据  加入条件进行查询
        Page<Brand> page = ( Page<Brand>)brandDao.selectByExample(query);
        return new PageResult(page.getTotal(),page.getResult());
    }


    /*//分页查询  传入的参数是当前页码和每页显示的条数
    public PageResult findPage(Integer pageNum, Integer pageSize) {
        //使用分页助手
        //初始化
        PageHelper.startPage(pageNum,pageSize);

       *//* List<Brand> brandList = brandDao.selectByExample(null);
        //PageInfo(以前使用的)  返回值就是pageInfo  类型 PageInfo<Brand>
        PageInfo<Brand> pageInfo = new PageInfo<>(brandList);*//*

        //Page也是分页助手的里的  这个类继承了ArraryList  查询的数据
        Page<Brand> page =(Page<Brand>) brandDao.selectByExample(null);
        //将查询的数据封装到PageResult中并返回
        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return pageResult;
    }*/

    @Override
    //增加品牌
    public void add(Brand brand) {
        //这个是插入方法，没有Selective，不会对字段值判断是否为null，为null一样会拼接到sql语句中，效率低
        // brandDao.insert(brand);
        //这个会对brand中的属性判断是否为空，为空不进行拼接sql语句，可以提高效率
        brandDao.insertSelective(brand);
    }

    @Override
    //品牌修改
    public void update(Brand brand) {
        //通过主见id修改数据同时判断字段是否为空  为空不进行拼接sql语句
        brandDao.updateByPrimaryKeySelective(brand);
    }

    @Override
    //品牌修改之前对该品牌进行回显
    public Brand findOne(Long id) {
        return brandDao.selectByPrimaryKey(id);
    }

    @Override
    //批量删除品牌
    public void delete(Long[] ids) {
        //通过遍历数组来一个个删除
        for (Long id : ids) {
            brandDao.deleteByPrimaryKey(id);
        }
    }

    @Override
    //模板管理添加其模板时回显品牌
    public List<Map> selectOptionList() {
        return brandDao.selectOptionList();
    }
}

package com.xyxy.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xyxy.core.dao.specification.SpecificationDao;
import com.xyxy.core.dao.specification.SpecificationOptionDao;
import com.xyxy.core.pojo.entity.PageResult;
import com.xyxy.core.pojo.entity.SpecEntity;
import com.xyxy.core.pojo.specification.Specification;
import com.xyxy.core.pojo.specification.SpecificationOption;
import com.xyxy.core.pojo.specification.SpecificationOptionQuery;
import com.xyxy.core.pojo.specification.SpecificationQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional  //执行多张表开启事务
public class SpecificationServiceImpl implements SpecificationService {
    @Autowired
    private SpecificationDao specDao;
    @Autowired
    private SpecificationOptionDao optionDao;

    @Override
    //查询所有
    public List<Specification> findAll() {
        return specDao.selectByExample(null);
    }

    @Override
    //分页查询  带条件
    public PageResult findPage(Specification specification, Integer pageNum, Integer rows) {
        //分页助手参数初始化
        PageHelper.startPage(pageNum,rows);
        //创建查询条件
        SpecificationQuery query = new SpecificationQuery();
        SpecificationQuery.Criteria criteria = query.createCriteria();
        if(specification!=null){
            if(specification.getSpecName()!=null && !"".equals(specification.getSpecName())){
                criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
            }
        }
        //查数据  添加条件
        Page<Specification> page = (Page<Specification>)specDao.selectByExample(query);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    //规格添加
    public void add(SpecEntity specEntity) {
        //这个实体了包含了2张表的内容  取出分别增加到数据库中
        //添加规格对象
        specDao.insertSelective(specEntity.getSpecification());

        //取出规格选项之后进行判断是否为空
        List<SpecificationOption> optionList = specEntity.getSpecificationOptionList();
        if (optionList!=null){
            //遍历进行一个个写入导数据库中
            for (SpecificationOption option : optionList) {
                //设置规格选项外键  是通过添加规格对象时把主键返回到对象中
                option.setSpecId(specEntity.getSpecification().getId());
                optionDao.insertSelective(option);
            }
        }
    }

    @Override
    //查询实体  根据id查询规格  根据外键id查询对应的规格选项
    public SpecEntity findOne(Long id) {
        SpecEntity specEntity = new SpecEntity();
        //查询规格名称封装到实体中
        specEntity.setSpecification(specDao.selectByPrimaryKey(id));

        //创建规格选项查询条件  是根据外键来查询
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = query.createCriteria();
        criteria.andSpecIdEqualTo(id);
        //查询对应的规格选项  封装到实体中
        List<SpecificationOption> optionList = optionDao.selectByExample(query);
        specEntity.setSpecificationOptionList(optionList);
        return specEntity;
    }

    @Override
    //规格修改
    public void update(SpecEntity specEntity) {
        //根据规格对象进行修改规格名称  根据id来修改
        specDao.updateByPrimaryKeySelective(specEntity.getSpecification());

        //规格选项修改  根据id外键删除规格选项表中对应的数据  然后在加入
        //创建删除规格选项的条件
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = query.createCriteria();
        criteria.andSpecIdEqualTo(specEntity.getSpecification().getId());
        //删除
        optionDao.deleteByExample(query);

        //判断是否为空  为空就不需要插入
        List<SpecificationOption> optionList = specEntity.getSpecificationOptionList();
        if (optionList!=null){
            for (SpecificationOption option : optionList) {
                //设置规格选项外键
                option.setSpecId(specEntity.getSpecification().getId());
                optionDao.insertSelective(option);
            }
        }
    }

    @Override
    //删除规格名称和对应的规格选项
    public void dele(Long[] ids) {
        //判断数组是否为空    ids是规格名称的主键数组
        if(ids!=null){
            //遍历删除规格名称和对应的规格选项
            for (Long id : ids) {
                //删除规格
                specDao.deleteByPrimaryKey(id);
                //删除规格选项  是根据外键条件删除  需要创建删除条件
                SpecificationOptionQuery query = new SpecificationOptionQuery();
                SpecificationOptionQuery.Criteria criteria = query.createCriteria();
                criteria.andSpecIdEqualTo(id);
                //删除规格选项
                optionDao.deleteByExample(query);
            }
        }
    }

    @Override
    //模板管理添加其模板时回显规格
    public List<Map> selectOptionList() {
        return specDao.selectOptionList();
    }


}

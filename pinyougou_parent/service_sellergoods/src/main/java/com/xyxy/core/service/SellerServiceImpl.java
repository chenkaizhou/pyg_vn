package com.xyxy.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xyxy.core.dao.seller.SellerDao;
import com.xyxy.core.pojo.entity.PageResult;
import com.xyxy.core.pojo.seller.Seller;
import com.xyxy.core.pojo.seller.SellerQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class SellerServiceImpl implements SellerService  {
    @Autowired
    private SellerDao sellerDao;

    @Override
    //保存注册的商家
    public void add(Seller seller){
        //注册时间
        seller.setCreateTime(new Date());
        //审核状态注册的时候是为0
        seller.setStatus("0");
        sellerDao.insertSelective(seller);
    }

    @Override
    //商家审核  分页查询  搜索
    public PageResult findPage(Seller seller, Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        SellerQuery query = new SellerQuery();
        SellerQuery.Criteria criteria = query.createCriteria();
        if(seller!=null){
            if(seller.getName()!=null && !"".equals(seller.getName())){
                criteria.andNameLike("%"+seller.getName()+"%");
            }
            if(seller.getNickName()!=null && !"".equals(seller.getNickName())){
                criteria.andNickNameLike("%"+seller.getNickName()+"%");
            }
            if (seller.getStatus() != null && !"".equals(seller.getStatus())) {
                criteria.andStatusEqualTo(seller.getStatus());
            }
        }
        Page<Seller> sellerPage =( Page<Seller>) sellerDao.selectByExample(query);
        return new PageResult(sellerPage.getTotal(),sellerPage.getResult());
    }

    @Override
    //查询实体  用于修改数据回显
    public Seller findOne(String id) {
        return sellerDao.selectByPrimaryKey(id);
    }

    @Override
    //修改商家状态
    public void updateStatus(String sellerId, String status) {
        Seller seller = new Seller();
        seller.setStatus(status);
        seller.setSellerId(sellerId);
        sellerDao.updateByPrimaryKeySelective(seller);
    }
}

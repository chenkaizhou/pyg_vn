package com.xyxy.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.xyxy.core.dao.address.AddressDao;
import com.xyxy.core.pojo.address.Address;
import com.xyxy.core.pojo.address.AddressQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressDao addressDao;

    @Override
    //根据用户名来查询住址信息
    public List<Address> findListByLoginUser(String userName) {
        AddressQuery query = new AddressQuery();
        AddressQuery.Criteria criteria = query.createCriteria();
        criteria.andUserIdEqualTo(userName);
        List<Address> addressList = addressDao.selectByExample(query);
        return addressList;
    }
}

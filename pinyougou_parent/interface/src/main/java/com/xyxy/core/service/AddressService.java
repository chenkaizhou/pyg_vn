package com.xyxy.core.service;

import com.xyxy.core.pojo.address.Address;

import java.util.List;

public interface AddressService {

    public List<Address> findListByLoginUser(String userName);
}

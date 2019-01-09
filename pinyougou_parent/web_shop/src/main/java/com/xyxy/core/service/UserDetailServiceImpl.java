package com.xyxy.core.service;

import com.xyxy.core.pojo.seller.Seller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

//自定义验证类，实现SpringSecurity框架的UserDetailService接口
public class UserDetailServiceImpl implements UserDetailsService {
    //需要sellerSevice  不能通过注解@Reference @Autowired注入  只能同过javabean注入（set方法）
    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       List<GrantedAuthority> authorityList = new ArrayList<>();//权限集合
        authorityList.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        //判断用户名是否为空
        if(username==null){
            return null;
        }
        //根据用户名到数据库中查询对应的用户对象  名字就是主键
        Seller seller = sellerService.findOne(username);
        //判断seller是否为null  为null不存在该用户
        if(seller!=null){
            //如果用户名对象查到，判断是否已经审核通过，未通过返回null，没有登录权限
            if("1".equals(seller.getStatus())){
                //返回SpringSecurity的User对象，将这个用户名 密码，所应该具有的访问权限集合返回
                return new User(username,seller.getPassword(),authorityList);
            }
        }
        return null;
    }
}

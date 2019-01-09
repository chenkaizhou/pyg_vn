package com.xyxy.core.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

//自定义认证类
public class UserDetailServiceImpl implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //创建权限集合
        List<GrantedAuthority> authorityList = new ArrayList<>();
        //向权限集合中加入访问权限
        authorityList.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new User(username,"",authorityList);
    }
}

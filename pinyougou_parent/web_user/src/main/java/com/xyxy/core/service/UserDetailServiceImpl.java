package com.xyxy.core.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/*自定义认证类
*在之前这里是负责用户名和密码的校验工作,并给当前用户赋予对应的访问权限
*现在cas和SpringSecurity集成,集成后,用户名和密码的校验工作交给cas完成,所以能够进入到
* 这里类的方法中的都是已经成功认证的用户,这里需要给登录过的用户赋予权限就可以
* */
public class UserDetailServiceImpl implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //创建权限集合对象
        List<GrantedAuthority> list = new ArrayList<>();
        //向权限集合中加入访问权限
        list.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new User(username,"",list);
    }
}

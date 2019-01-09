package com.xyxy.core.service;

import com.xyxy.core.pojo.user.User;

public interface UserService {
    public void sendCode(String phone);
    public Boolean checkSmsCode(String phone,String smsCode);
    public void add(User user);
}

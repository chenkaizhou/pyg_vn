package com.xyxy.core.pojo.entity;

import java.io.Serializable;

//返回结果封装
public class Result implements Serializable {
    //增删改查操作如果是成功为true，失败就为false
    private boolean success;
    //返回结果的信息
    private String message;

    public Result(boolean success, String message) {
        super();
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

package com.xyxy.core.pojo.entity;

import java.io.Serializable;
import java.util.List;

//分页结果封装对象
public class PageResult implements Serializable {
    //总记录数
    private Long total;
    //当前页结果
    private List rows; //泛型不指定类型是为了之后都可以使用
    public PageResult(Long total,List rows){
        super();
        this.total = total;
        this.rows = rows;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "PageResult{" +
                "total=" + total +
                ", rows=" + rows +
                '}';
    }
}

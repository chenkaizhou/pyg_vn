package com.xyxy.core.dao.item;

import com.xyxy.core.pojo.item.Item;
import com.xyxy.core.pojo.item.ItemQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ItemDao {
    int countByExample(ItemQuery example);

    int deleteByExample(ItemQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(Item record);

    int insertSelective(Item record);

    List<Item> selectByExample(ItemQuery example);

    Item selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Item record, @Param("example") ItemQuery example);

    int updateByExample(@Param("record") Item record, @Param("example") ItemQuery example);

    int updateByPrimaryKeySelective(Item record);

    int updateByPrimaryKey(Item record);
}
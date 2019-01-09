package com.xyxy.core.dao.template;

import com.xyxy.core.pojo.template.TypeTemplate;
import com.xyxy.core.pojo.template.TypeTemplateQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TypeTemplateDao {
    int countByExample(TypeTemplateQuery example);

    int deleteByExample(TypeTemplateQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(TypeTemplate record);

    int insertSelective(TypeTemplate record);

    List<TypeTemplate> selectByExample(TypeTemplateQuery example);

    TypeTemplate selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TypeTemplate record, @Param("example") TypeTemplateQuery example);

    int updateByExample(@Param("record") TypeTemplate record, @Param("example") TypeTemplateQuery example);

    int updateByPrimaryKeySelective(TypeTemplate record);

    int updateByPrimaryKey(TypeTemplate record);

    //查询所有
    List<Map> selectOptionList();
}
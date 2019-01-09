package com.xyxy.core.service;

import java.util.Map;

public interface CmsService {
    //使用freemarker创建静态Html页面
    public void createStaticPage(Long goodsId, Map<String, Object> rootMap) throws Exception;

    public Map<String, Object> findGoodsData(Long goodsId);
}

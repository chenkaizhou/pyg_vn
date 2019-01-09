package com.xyxy.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xyxy.core.pojo.ad.Content;
import com.xyxy.core.service.ContentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {

    @Reference
    private ContentService contentService;

    @RequestMapping("/findByCategoryId")
    public List<Content> findByCategoryId(Long categoryId){
        List<Content> contentList = contentService.findByCategoryId(categoryId);
        return  contentList;
    }
}

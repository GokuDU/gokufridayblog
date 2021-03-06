package com.guo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController extends BaseController{

    // 首页
    @RequestMapping({"","/","/index"})
    public String index(){
        // request.getParameter(name) == null   return defaultVal
        // 当前页 默认为 1  页面大小 默认为 2

        // 1.分页信息 2.分类  3.用户  4.置顶  5.精选(精华)  6.排序
        IPage pageResults = postService.paging(getPage(),null,null,null,null,"created");

        req.setAttribute("isRecommendHighLight", 0); // 0 全部博客  1 精华博客
        req.setAttribute("isNewOrHost", 0);     // 0 最新博客   1 热议博客
        req.setAttribute("pageData", pageResults);
        req.setAttribute("currentCategoryId", 0);
        return "index";
    }

    // 精华
    @RequestMapping("/betterPostIndex")
    public String betterPostIndex(){

        // 1.分页信息 2.分类  3.用户  4.置顶  5.精选(精华)  6.排序
        IPage pageResults = postService.paging(getPage(),null,null,null,true,"created");

        req.setAttribute("isRecommendHighLight", 1);
        req.setAttribute("pageData", pageResults);
        req.setAttribute("currentCategoryId", 0);
        return "index";
    }

    // 热度
    @RequestMapping("/hostPostIndex")
    public String hostPostIndex(){

        // 1.分页信息 2.分类  3.用户  4.置顶  5.精选(精华)  6.排序
        IPage pageResults = postService.paging(getPage(),null,null,null,null,"comment_count");

        req.setAttribute("isNewOrHost", 1);
        req.setAttribute("isRecommendHighLight", 0);
        req.setAttribute("pageData", pageResults);
        req.setAttribute("currentCategoryId", 0);
        return "index";
    }

    // 搜索
    @RequestMapping("/search")
    public String search(String q) {

        IPage searchPageData = searchService.search(getPage(),q);

        req.setAttribute("q", q);
        req.setAttribute("searchPageData", searchPageData);
        return "search";
    }
}

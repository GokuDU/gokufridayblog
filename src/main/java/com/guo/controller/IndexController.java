package com.guo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController extends BaseController{

    @RequestMapping({"","/","/index"})
    public String index(){
        // request.getParameter(name) == null   return defaultVal
        // 当前页 默认为 1  页面大小 默认为 2

        // 1.分页信息 2.分类  3.用户  4.置顶  5.精选(精华)  6.排序
        IPage pageResults = postService.paging(getPage(),null,null,null,null,"created");

        req.setAttribute("pageData", pageResults);
        req.setAttribute("currentCategoryId", 0);
        return "index";
    }
}

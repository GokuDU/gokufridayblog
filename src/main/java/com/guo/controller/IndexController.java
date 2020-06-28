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
        int pn = ServletRequestUtils.getIntParameter(req, "pn",1);
        int size = ServletRequestUtils.getIntParameter(req, "size",2);
        Page page = new Page(pn,size);

        // 1.分页信息 2.分类  3.用户  4.置顶  5.精选(精华)  6.排序
        IPage results = postService.paging(page,null,null,null,null,"created");

        req.setAttribute("pageData", results);
        req.setAttribute("currentCategoryId", 0);
        return "index";
    }
}

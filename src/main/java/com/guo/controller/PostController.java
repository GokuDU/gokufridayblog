package com.guo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PostController extends BaseController{

    // :\d*  指定接收数字参数类型
    @RequestMapping("/category/{id:\\d*}")
    public String category(@PathVariable(name = "id") Long id){
        req.setAttribute("currentCategoryId", id);
        return "post/category";
    }

    @RequestMapping("/post/{id:\\d*}")
    public String detail(@PathVariable(name = "id") Long id){
        return "post/detail";
    }
}

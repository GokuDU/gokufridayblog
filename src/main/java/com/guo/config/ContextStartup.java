package com.guo.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guo.entity.Category;
import com.guo.mapper.CategoryMapper;
import com.guo.service.CategoryService;
import com.guo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import java.util.List;

@Component
public class ContextStartup implements ApplicationRunner, ServletContextAware {

    @Autowired
    CategoryService categoryService;

    // 注入 全局上下文
    @Autowired
    ServletContext servletContext;

    @Autowired
    PostService postService;

    // 项目启动的时候就会调用 run
    // 查询 status = 0 的分类列表
    //
    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Category> categories = categoryService.list(new QueryWrapper<Category>()
                .eq("status", 0)
        );
        // 存储全局数据，注入到应用
        servletContext.setAttribute("categories",categories);

        // 初始化本周热议
        postService.initWeekRank();
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}

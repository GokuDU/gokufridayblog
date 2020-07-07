package com.guo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.guo.vo.CommentVO;
import com.guo.vo.PostVO;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PostController extends BaseController{

    // :\d*  指定接收数字参数类型
    @RequestMapping("/category/{id:\\d*}")
    public String category(@PathVariable(name = "id") Long id){

        int pn = ServletRequestUtils.getIntParameter(req, "pn", 1);

        req.setAttribute("pn", pn);
        req.setAttribute("currentCategoryId", id);
        return "post/category";
    }

    @RequestMapping("/post/{id:\\d*}")
    public String detail(@PathVariable(name = "id") Long id){

        PostVO postVO = postService.selectOnePost(new QueryWrapper<>().eq("p.id", id));
        Assert.notNull(postVO, "文章已被删除");

        postService.putViewCount(postVO);

        // 1. 分页    2.文章id    3. 用户id    4. 排序
        IPage<CommentVO> commentResults  = commentService.paging(getPage(),postVO.getId(),null,"created");

        req.setAttribute("currentCategoryId", postVO.getCategoryId());
        req.setAttribute("post", postVO);
        req.setAttribute("commentPageData", commentResults);

        return "post/detail";
    }
}

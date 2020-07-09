package com.guo.controller;

import com.guo.common.lang.Result;
import com.guo.entity.Post;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController{

    @ResponseBody
    @PostMapping("/jie-set")
    public Result jieSet(Long id,Integer rank,String field) {
        Post post = postService.getById(id);
        Assert.isTrue(post != null, "该文章不存在");

        if ("delete".equals(field)) {
            // 删除
            postService.removeById(id);
            return Result.success();

        } else if ("stick".equals(field)) {
            // 置顶
            post.setLevel(rank);

        } else if ("status".equals(field)) {
            // 加精
            post.setRecommend(rank == 1);

        }

        postService.updateById(post);
        return Result.success();
    }

}

package com.guo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guo.common.lang.Result;
import com.guo.config.RabbitMqConfig;
import com.guo.entity.Post;
import com.guo.search.mq.entity.PostMqIndexMessage;
import com.guo.vo.PostVO;
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

            // 通知消息个mq ，告知删除
            amqpTemplate.convertAndSend(RabbitMqConfig.ES_EXCHANGE, RabbitMqConfig.ES_ROUTING_KEY,
                    new PostMqIndexMessage(post.getId(), PostMqIndexMessage.REMOVE));

            return Result.success().action("/user/index");

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

    // es 数据同步
    @ResponseBody
    @PostMapping("/initEsData")
    public Result initEsData() {

        // 一页 10000 条
        int size = 10000;
        Page page = new Page();
        page.setSize(size);
        // 同步数据总条数
        long total = 0;

        for (int i = 1; i < 1000 ; i++) {
            page.setCurrent(i);

            IPage<PostVO> paging = postService.paging(page, null, null, null, null, null);

            int nums = searchService.initEsData(paging.getRecords());
            total += nums;

            // 当一页查询不出 10000 条数据 ， 说明不足一页了
            if (paging.getRecords().size() < size) {
                break;
            }
        }

        return Result.success("Es索引初始化成功"+ total +"条记录",null);
    }

}

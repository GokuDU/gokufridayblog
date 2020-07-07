package com.guo.schedules;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guo.entity.Post;
import com.guo.service.PostService;
import com.guo.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class ViewCountSyncTask {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    PostService postService;

    @Scheduled(cron = "0/5 * * * * *")  // 每5秒同步一次
    public void task() {
        Set<String> keys = redisTemplate.keys("rank:post:*");

        // 保存 需要更新阅读量的 博客id
        List<String> ids = new ArrayList<>();

        for (String key : keys) {
            // 判断hash表中是否有该项的值
            if (redisUtil.hHasKey(key,"post:viewCount")) {
                // 截取保存 postId 到 ids集合中
                ids.add(key.substring("rank:post:".length()));
            }
        }

        // ids 集合为空，直接返回
        if (ids.isEmpty())
            return;

        // 获取需要更新阅读了的博客 (ids 存放更新更新阅读量的 postId)
        List<Post> posts = postService.list(new QueryWrapper<Post>().in("id", ids));

        posts.stream().forEach((post) -> {
            Integer viewCount = (Integer) redisUtil.hget("rank:post:" + post.getId(), "post:viewCount");
            post.setViewCount(viewCount);
        });

        // post 集合为空，直接返回 （比如：评论更新之后，文章删除）
        if (posts.isEmpty())
            return;

        // 更新，同步到数据库
        boolean isSucc = postService.updateBatchById(posts);

        // 如果更新成功，把 key rank:post:[postId] 删掉
        // 在刷新页面的时候 走 new PostServiceImpl.initWeekRank();
        //     会把更新到数据库的 viewCount重新 通过 key rank:post:[postId]  给  hset 到redis
        if(isSucc) {
            ids.stream().forEach((id) -> {
                redisUtil.hdel("rank:post:"+id, "post:viewCount");
                System.out.println(id + "-------------->同步成功");
            });
        }
    }
}

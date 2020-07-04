package com.guo.template;

import com.guo.common.templates.DirectiveHandler;
import com.guo.common.templates.TemplateDirective;
import com.guo.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *  本周热议
 */
public class HostsTemplate extends TemplateDirective {

    @Autowired
    RedisUtil redisUtil;

    @Override
    public String getName() {
        return "hots";
    }

    @Override
    public void execute(DirectiveHandler handler) throws Exception {
        String weekRankKey = "week:rank";

        // getZSetRank(String key, long start, long end)
        Set<ZSetOperations.TypedTuple> typedTuples = redisUtil.getZSetRank(weekRankKey, 0, 6);

        for (ZSetOperations.TypedTuple typedTuple : typedTuples) {
            Map<String,Object> map = new HashMap<>();

            // value 即 postId
            // 在 PostServiceImpl 的 initWeekRank()中
            //      循环 List<Post> --》 redisUtil.zSet(key, post.getId(), post.getCommentCount());
            Object value = typedTuple.getValue();
            String postHashKey = "rank:post:" + value;

            map.put("id",value);
            map.put("title",redisUtil.hget(postHashKey,"post:title"));

        }

    }
}

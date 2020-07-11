package com.guo.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guo.entity.Post;
import com.guo.entity.User;
import com.guo.mapper.PostMapper;
import com.guo.service.PostService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guo.util.RedisUtil;
import com.guo.vo.PostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author gokufriday
 * @since 2020-06-13
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Autowired
    PostMapper postMapper;

    @Autowired
    RedisUtil redisUtil;

    /**
     * 博客分页
     */
    @Override
    public IPage<PostVO> paging(Page page, Long categoryId, Long userId, Integer level, Boolean recommend, String order) {

        if(level == null)
            level = -1;

        QueryWrapper wrapper = new QueryWrapper<Post>()
                .eq(categoryId != null,"category_id", categoryId)
                .eq(userId != null,"user_id", userId)   // 当这个id不为空的时候 才有后面的条件
                .eq(level == 0,"level", 0)          // 这里想让数值越大越置顶 ， 默认为0
                .gt(level > 0,"level", 0)       //  level大于0  置顶
                .orderByDesc(order != null,order);      // order是Controller传进来的对应博客表的创建时间 created

        return postMapper.selectPosts(page,wrapper);
    }

    /**
     * 博客详情
     */
    @Override
    public PostVO selectOnePost(QueryWrapper<Post> wrapper) {
        return postMapper.selectOnePost(wrapper);
    }

    /**
     *  本周热议初始化
     */
    @Override
    public void initWeekRank() {
        // 获取7天内发表的文章（严谨点应该是7天内评论过的文章）
        List<Post> posts = this.list(new QueryWrapper<Post>()
                .ge("created", DateUtil.offsetDay(new Date(), -7))  // 假如今天18号  ==》 11号（过去七天的开始）
//                .ge("created", DateUtil.lastWeek())
                .select("id,title,user_id,view_count,comment_count,created")
        );

        // 初始化文章的总评论量
        // 缓存文章的一些基本信息 （id、标题、评论数量、作者）
        for (Post post : posts) {
            // PURE_DATE_PATTERN = "yyyyMMdd"
            String key = "day:rank:" + DateUtil.format(post.getCreated(), DatePattern.PURE_DATE_FORMAT);

            // redisTemplate.opsForZSet().add(key, value, score);
            // ZADD key score member[value]
            redisUtil.zSet(key, post.getId(), post.getCommentCount());

            // 7天后自动过期 （10号发表 7-（14-10）= 3 天 ）
            long between = DateUtil.between(post.getCreated(), new Date(), DateUnit.DAY);
            long exipreTime = (7 - between) * 24 * 60 * 60;    // 有效期时间

            // 设置 key  day:rank:yyyyMMdd 存活时间
            redisUtil.expire(key, exipreTime);

            // 缓存文章的一些基本信息 （id、标题、评论数量、作者）
            this.hashCachePostIdAndTitle(post,exipreTime);

        }

        // 做并集
        this.zunionAndStoreLastWeekRank();
    }

    /**
     *  缓存文章的一些基本信息 （id、标题、评论数量、作者）
     */
    private void hashCachePostIdAndTitle(Post post,long exipreTime) {
        String key = "rank:post:" + post.getId();
        boolean hasKey = redisUtil.hasKey(key);
        // 如果 key 已经存在了，就不进这个方法
        if (!hasKey) {
            // hset(String key, String item, Object value, long time)
            // hset key field[item] value [field value ...]
            redisUtil.hset(key, "post:id", post.getId(),exipreTime);
            redisUtil.hset(key, "post:title", post.getTitle(), exipreTime);
            redisUtil.hset(key, "post:commentCount", post.getCommentCount(), exipreTime);
            redisUtil.hset(key, "post:viewCount", post.getViewCount(), exipreTime);
        }
    }

    /**
     *  过去一周合并每日评论数量操作（并集）
     */
    private void zunionAndStoreLastWeekRank() {
        String destKey = "week:rank";
        // PURE_DATE_PATTERN = "yyyyMMdd"
        String currentKey = "day:rank:" + DateUtil.format(new Date(), DatePattern.PURE_DATE_FORMAT);

        List<String> otherKeys = new ArrayList<>();
        for (int i = -6; i < 0 ; i++) {
            String temp = "day:rank:" +
                    DateUtil.format(DateUtil.offsetDay(new Date(), i), DatePattern.PURE_DATE_FORMAT);

            otherKeys.add(temp);
        }

        // currentKey + otherKeys = destKey
        // ZUNIONSTORE destination numkeys key [key ...]
        redisUtil.zUnionAndStore(currentKey, otherKeys , destKey);

    }

    /**
     *  添加新评论 --》 缓存刷新
     *  在添加一条新的评论之后，不仅仅只是添加，还要让过去一周合并每日评论数量操作（并集）
     */
    @Override
    public void incrCommentCountAndUnionForWeekRank(long postId, boolean isIncr) {
        String currentKey = "day:rank:" + DateUtil.format(new Date(), DatePattern.PURE_DATE_FORMAT);
        // zIncrementScore(String key, Object value, long delta)
        // ZINCRBY key increment[delta] member[value]
        redisUtil.zIncrementScore(currentKey,postId,isIncr ? 1 : -1);

        Post post = this.getById(postId);

        // 7天后自动过期 （10号发表 7-（14-10）= 3 天 ）
        long between = DateUtil.between(post.getCreated(), new Date(), DateUnit.DAY);
        long exipreTime = (7 - between) * 24 * 60 * 60;    // 有效期时间

        // 缓存文章的一些基本信息 （id、标题、评论数量、作者）
        this.hashCachePostIdAndTitle(post,exipreTime);

        // 重新做并集
        this.zunionAndStoreLastWeekRank();
    }

    @Override
    public void putViewCount(PostVO postVO) {
        String key = "rank:post:" + postVO.getId();
        // 1. 从缓存中获取viewCount
        Integer viewCount = (Integer) redisUtil.hget(key, "post:viewCount");
        // 2. 如果没有，就先从实体里获取，再 +1
        if (viewCount != null) {
            // 如果缓存中有，就从缓存中获取，再 +1
            postVO.setViewCount(viewCount + 1);
        } else {
            postVO.setViewCount(postVO.getViewCount() + 1);
        }
        // 3. 同步到缓存里
        redisUtil.hset(key, "post:viewCount", postVO.getViewCount());
    }

}

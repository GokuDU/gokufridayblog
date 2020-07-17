package com.guo.controller;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.guo.common.lang.Consts;
import com.guo.common.lang.Result;
import com.guo.config.RabbitMqConfig;
import com.guo.entity.*;
import com.guo.search.mq.entity.PostMqIndexMessage;
import com.guo.util.ValidationUtil;
import com.guo.vo.CommentVO;
import com.guo.vo.PostVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@Controller
public class PostController extends BaseController{

    // :\d*  指定接收数字参数类型 (分类博客)
    @RequestMapping("/category/{id:\\d*}")
    public String category(@PathVariable(name = "id") Long id){

        int pn = ServletRequestUtils.getIntParameter(req, "pn", 1);

        // 1.分页信息 2.分类  3.用户  4.置顶  5.精选(精华)  6.排序
        IPage pageResults = postService.paging(getPagePlus(),id,null,null,null,"created");

        req.setAttribute("currentCategoryId", id);
        req.setAttribute("categoryData", pageResults);

        log.info("categryId----------->"+ id);
        return "post/category";
    }

    @RequestMapping("/post/{id:\\d*}")
    public String detail(@PathVariable(name = "id") Long id){

        PostVO postVO = postService.selectOnePost(new QueryWrapper<Post>().eq("p.id", id));
        Assert.notNull(postVO, "文章已被删除");

        postService.putViewCount(postVO);

        // 1. 分页    2.文章id    3. 用户id    4. 排序
        IPage<CommentVO> commentResults  = commentService.paging(getPagePlus(),postVO.getId(),null,"created");

        req.setAttribute("currentCategoryId", postVO.getCategoryId());
        req.setAttribute("post", postVO);
        req.setAttribute("commentPageData", commentResults);

        return "post/detail";
    }

    /**
     *  判断用户是否收藏了文章
     */
    @ResponseBody
    @PostMapping("/collection/find")
    public Result collectionFind(Long pid) {

        int count = userCollectionService.count(new QueryWrapper<UserCollection>()
                .eq("user_id", getProfileId())
                .eq("post_id", pid)
        );

        return Result.success(MapUtil.of("collection", count > 0));
    }

    /**
     *  收藏文章
     */
    @ResponseBody
    @PostMapping("/collection/add")
    public Result collectionAdd(Long pid) {

        Post post = postService.getById(pid);

        // 使用断言判断 post[文章] 是否不为空
        // 而如果文章为空 expression 就为 false ，则会抛出异常
        //  if (!expression) {
        //			throw new IllegalArgumentException(message);
        //	}
        Assert.isTrue(post != null, "该文章已经被删除了");

        // 如果用户已经收藏
        int count = userCollectionService.count(new QueryWrapper<UserCollection>()
                .eq("user_id", getProfileId())
                .eq("post_id", pid)
        );
        if (count > 0) {
            return Result.fail("您已经收藏了");
        }

        // 添加一条收藏数据
        UserCollection userCollection = new UserCollection();
        userCollection.setUserId(getProfileId());
        userCollection.setPostId(pid);
        userCollection.setCreated(new Date());
        userCollection.setModified(new Date());
        userCollection.setPostUserId(post.getUserId());

        userCollectionService.save(userCollection);

        return Result.success();
    }

    /**
     *  取消收藏文章
     */
    @ResponseBody
    @PostMapping("/collection/remove")
    public Result collectionRemove(Long pid) {

        Post post = postService.getById(pid);
        Assert.isTrue(post != null, "该文章已经被删除了");

        // 删除一条收藏数据
        userCollectionService.remove(new QueryWrapper<UserCollection>()
                .eq("user_id", getProfileId())
                .eq("post_id", pid)
        );

        return Result.success();
    }

    // 跳到编辑博客
    @GetMapping("/post/edit")
    public String edit() {
        String postId = req.getParameter("id");
        if (!StringUtils.isEmpty(postId)) {
            Post post = postService.getById(postId);
            Assert.isTrue(post != null, "该文章不存在");
            Assert.isTrue(post.getUserId() == (getProfileId()), "sorry，您没有权限编辑此文章");
            req.setAttribute("post", post);
        }

        req.setAttribute("categories", categoryService.list());
        return "/post/edit";
    }

    // 发布已编辑好的博客
    @ResponseBody
    @PostMapping("/post/submit")
    public Result submit(Post post) {
        // 校验博客
        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(post);
        if (validResult.hasErrors()) {
            return Result.fail(validResult.getErrors());
        }

        // 如果是发表新文章
        if (post.getId() == null) {
            post.setUserId(getProfileId());
            post.setModified(new Date());
            post.setCreated(new Date());
            post.setCommentCount(0);
            post.setEditMode(null);
            post.setLevel(0);
            post.setRecommend(false);
            post.setViewCount(0);
            post.setVoteDown(0);
            post.setVoteUp(0);

            postService.save(post);
        } else {
            Post postTemp = postService.getById(post.getId());
            Assert.isTrue(postTemp.getUserId() == getProfileId(), "sorry，您没有权限编辑此文章");

            postTemp.setTitle(post.getTitle());
            postTemp.setContent(post.getContent());
            postTemp.setCategoryId(post.getCategoryId());

            postService.updateById(postTemp);
        }

        // 通知消息个mq ，告知添加或更新
        amqpTemplate.convertAndSend(RabbitMqConfig.ES_EXCHANGE, RabbitMqConfig.ES_ROUTING_KEY,
                new PostMqIndexMessage(post.getId(), PostMqIndexMessage.CREATE_OR_UPDATE));

        return Result.success().action("/post/"+post.getId());
    }

    // 刪除博客
    @ResponseBody
    @Transactional
    @PostMapping("/post/delete")
    public Result delete(Long id) {
        Post post = postService.getById(id);
        Assert.isTrue(post != null, "该文章不存在");
        Assert.isTrue(post.getUserId() == (getProfileId()), "sorry，您没有权限删除此文章");

        postService.removeById(id);

        // 删除相关消息、收藏
        userMessageService.removeByMap(MapUtil.of("post_id", id));
        userCollectionService.removeByMap(MapUtil.of("post_id", id));

        // 通知消息个mq ，告知删除
        amqpTemplate.convertAndSend(RabbitMqConfig.ES_EXCHANGE, RabbitMqConfig.ES_ROUTING_KEY,
                new PostMqIndexMessage(post.getId(), PostMqIndexMessage.REMOVE));

        return Result.success("删除成功", null).action("/user/index");
    }

    // 评论博客
    @ResponseBody
    @Transactional
    @PostMapping("/post/reply")
    public Result reply(Long postId,String content) {
        Post post = postService.getById(postId);
        Assert.isTrue(post != null, "该文章不存在");
        Assert.hasLength(content, "评论内容不能为空");

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setContent(content);
        comment.setUserId(getProfileId());
        comment.setCreated(new Date());
        comment.setModified(new Date());
        comment.setLevel(0);
        comment.setVoteUp(0);
        comment.setVoteDown(0);
        commentService.save(comment);

        // 博客评论量 +1
        post.setCommentCount(post.getCommentCount() + 1);
        postService.updateById(post);

        // 本周热议更新(数量+1)   true ? +1 : -1
        postService.incrCommentCountAndUnionForWeekRank(post.getId(), true);

        /**
         * 评论通知
         */
        // 通知作者 ， 有人评论了你的文章
        // 先判断评论用户 和 博客作者 是否为同一个人
        // 作者评论自己的博客 不需要通知
        if (comment.getUserId() != post.getUserId()){
            UserMessage userMessage = new UserMessage();
            userMessage.setPostId(postId);
            userMessage.setCommentId(comment.getId());
            userMessage.setFromUserId(getProfileId());
            userMessage.setToUserId(post.getUserId());
            // 消息类型 ： 0 系统消息   1 评论文章   2 回复评论
            userMessage.setType(1);
            userMessage.setContent(content);
            userMessage.setCreated(new Date());
            // 未读消息 0
            userMessage.setStatus(0);
            userMessageService.save(userMessage);

            // 即时通知作者 （WebSocket）
            webSocketService.sendInstantMessageCountToUser(userMessage.getToUserId());
        }

        // 通知被 @ 的人，有人回复了你
        if (content.startsWith("@")) {
            String username = content.substring(1, content.indexOf(" "));
            System.out.println(username);

            // 查出来 被@ 的用户
            User user = userService.getOne(new QueryWrapper<User>().eq("username", username));
            if (user != null && user.getId() != getProfileId()) {
                UserMessage userMessage = new UserMessage();
                userMessage.setPostId(postId);
                userMessage.setCommentId(comment.getId());
                userMessage.setFromUserId(getProfileId());
                userMessage.setToUserId(user.getId());
                // 消息类型 ： 0 系统消息   1 评论文章   2 回复评论
                userMessage.setType(2);
                userMessage.setContent(content);
                userMessage.setCreated(new Date());
                // 未读消息 0
                userMessage.setStatus(0);
                userMessageService.save(userMessage);

                // 即时通知 被@ 的用户
                webSocketService.sendInstantMessageCountToUser(userMessage.getToUserId());
            }
        }

        return Result.success().action("/post/"+postId);
    }


    // 删除评论
    @ResponseBody
    @Transactional
    @PostMapping("/post/delReply")
    public Result delReply(Long id) {
        Assert.notNull(id, "当前评论id为空");
        Comment comment = commentService.getById(id);
        Assert.notNull(comment, "找不到对应评论");
        if(comment.getUserId() != getProfileId()) {
            return Result.fail("不是你发表的评论！");
        }
        commentService.removeById(id);

        // 评论数量 -1
        Post post = postService.getById(comment.getPostId());
        post.setCommentCount(post.getCommentCount() - 1);
        postService.updateById(post);

        // 本周热议 -1
        postService.incrCommentCountAndUnionForWeekRank(comment.getPostId(), false);

        return Result.success("删除成功").action("/post/"+post.getId());
    }

    // 点赞评论
    @ResponseBody
    @Transactional
    @PostMapping("/post/zanReply")
    public Result zanReply(Long id) {
//        Assert.notNull(getProfileId(), "请登录之后再点赞");

        Assert.notNull(id, "当前评论id为空");
        Comment comment = commentService.getById(id);

        Assert.notNull(comment, "找不到对应评论");
        Post post = postService.getById(comment.getPostId());
//
//        SELECT ua.*
//        FROM `user_action` ua
//        WHERE ua.`user_id` = 8
//        AND ua.`comment_id` = 1281869011889430530
        int count = userActionService.count(new QueryWrapper<UserAction>()
                .eq("user_id", getProfileId())
                .eq("comment_id", id)
        );

        if (count == 0) {

            UserAction userActionTemp = new UserAction();
            userActionTemp.setUserId(getProfileId());
            userActionTemp.setPostId(comment.getPostId());
            userActionTemp.setCommentId(id);
            userActionTemp.setPoint(1);
            userActionTemp.setCreated(new Date());
            userActionService.save(userActionTemp);

            comment.setVoteUp(comment.getVoteUp()+1);
            commentService.updateById(comment);

            // 点赞通知
            Long commentUserId = comment.getUserId();

            User commentUser = userService.getOne(new QueryWrapper<User>()
                    .eq("id", commentUserId)
            );

            // 自己点赞自己不通知
            if (commentUser != null && commentUserId != getProfileId()) {
                UserMessage userMessage = new UserMessage();
                userMessage.setPostId(comment.getPostId());
                userMessage.setCommentId(comment.getId());
                userMessage.setContent(comment.getContent());
                userMessage.setFromUserId(getProfileId());
                userMessage.setToUserId(commentUserId);
                // 消息类型 ： 0 系统消息   1 评论文章   2 回复评论  3 点赞
                userMessage.setType(3);
                userMessage.setCreated(new Date());
                // 未读消息 0
                userMessage.setStatus(0);
                userMessageService.save(userMessage);

                userActionTemp.setAction(Consts.LIKE_COMMENT);
                userActionService.updateById(userActionTemp);

                // 即时通知 被点赞 的用户
                webSocketService.sendInstantMessageCountToUser(userMessage.getToUserId());
            }

            return Result.success().action("/post/"+post.getId());
        }

        UserAction userAction = userActionService.getOne(new QueryWrapper<UserAction>()
                .eq("user_id", getProfileId())
                .eq("comment_id", id)
        );

        if (userAction.getPoint().equals(1)) {
//                userAction.setPoint(0);
//                userAction.setModified(new Date());
//                userActionService.updateById(userAction);
//
//                comment.setVoteDown(comment.getVoteDown()+1);
//                userAction.setModified(new Date());
//                commentService.updateById(comment);
//                return Result.success().action("/post/"+post.getId());
            return Result.fail("您已经点过赞了");
        } else if (userAction.getPoint().equals(0)) {
            return Result.fail("您之前已经点过赞了");
        }

        return Result.success().action("/post/"+post.getId());
    }

}

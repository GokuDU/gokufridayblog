package com.guo.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.guo.common.lang.Result;
import com.guo.entity.Post;
import com.guo.entity.User;
import com.guo.entity.UserMessage;
import com.guo.shiro.AccountProfile;
import com.guo.util.UploadUtil;
import com.guo.vo.UserMessageVO;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class UserController extends BaseController{

    @Autowired
    UploadUtil uploadUtil;

    // 跳到我的主页
    @GetMapping("/user/home")
    public String home() {

        User user = userService.getById(getProfileId());

        List<Post> posts = postService.list(new QueryWrapper<Post>()
                .eq("user_id", getProfileId())
                // 30天内发布的博客
                .gt("created", DateUtil.offsetDay(new Date(), -30))
                .orderByDesc("created")
        );

        req.setAttribute("user", user);
        req.setAttribute("posts", posts);

        return "/user/home";
    }

    // 查看别人主页
    @GetMapping({"/user/{id:\\d*}","/post/user/{id:\\d*}"})
    public String userHome(@PathVariable(name = "id") Long id) {

        User user = userService.getById(id);

        List<Post> posts = postService.list(new QueryWrapper<Post>()
                .eq("user_id", id)
                // 30天内发布的博客
                .gt("created", DateUtil.offsetDay(new Date(), -30))
                .orderByDesc("created")
        );

        req.setAttribute("user", user);
        req.setAttribute("posts", posts);

        return "/user/home";
    }

    // 跳到基本设置页
    @GetMapping("/user/set")
    public String set() {
        User user = userService.getById(getProfileId());
        req.setAttribute("user", user);

        return "/user/set";
    }

    // 基本设置修改
    @ResponseBody
    @PostMapping("/user/set")
    public Result doSet(User user) {

        // 头像不为空，就可以更新头像
        if (StrUtil.isNotBlank(user.getAvatar())) {
            User userTemp = userService.getById(getProfileId());
            userTemp.setAvatar(user.getAvatar());
            userService.updateById(userTemp);

            AccountProfile accountProfile = getProfile();
            accountProfile.setAvatar(userTemp.getAvatar());

            // accountProfile 放到session 中
            SecurityUtils.getSubject().getSession().setAttribute("profile", accountProfile);

            return Result.success().action("/user/set#avatar");
        }

        // 校验昵称，该项必填
        if (StrUtil.isEmpty(user.getUsername()))
            return Result.fail("昵称不能为空");
        // 检查昵称是否已经被使用 、 查询要排除自己
        int count = userService.count(new QueryWrapper<User>()
                .eq("username", getProfile().getUsername())
                .ne("id", getProfileId())
        );
        if (count >0) {
            return Result.fail("该昵称已经被使用了");
        }

        User userTemp = userService.getById(getProfileId());
        userTemp.setUsername(user.getUsername());
        userTemp.setGender(user.getGender());
        userTemp.setSign(user.getSign());
        userService.updateById(userTemp);

        // 因为用户登录数据 存放在 AccountProfile （在头栏目填充数据） ，需要更新这个数据
        AccountProfile accountProfile = getProfile();
        accountProfile.setUsername(userTemp.getUsername());
        accountProfile.setSign(userTemp.getSign());
        accountProfile.setGender(userTemp.getGender());

        // accountProfile 放到session 中
        SecurityUtils.getSubject().getSession().setAttribute("profile", accountProfile);

        return Result.success().action("/user/set#info");
    }


    // 上传头像
    @ResponseBody
    @PostMapping("/user/upload")
    public Result uploadAvatar(@RequestParam(value = "file") MultipartFile file) throws IOException {

        return uploadUtil.upload(UploadUtil.type_avatar,file);
    }

    // 修改密码
    @ResponseBody
    @PostMapping("/user/repass")
    public Result rePass(String nowpass,String pass,String repass) {
        // 检查两次输入的密码
        if (!pass.equals(repass)) {
            return Result.fail("两次输入的密码不同,请重新输入");
        }

        User user = userService.getById(getProfileId());

        // 检查原密码输入
        String nowPassMd5 = SecureUtil.md5(nowpass);
        if (!nowPassMd5.equals(user.getPassword())) {
            return Result.fail("输入原密码不正确");
        }

        user.setPassword(SecureUtil.md5(pass));
        userService.updateById(user);

        // 修改成功，退出登录
        return Result.success().action("/user/logout");
    }

    // 跳到用户中心
    @GetMapping("/user/index")
    public String index() {
        User user = userService.getById(getProfileId());
        req.setAttribute("user", user);

        // 发表文章数量
        int publishCount = postService.count(new QueryWrapper<Post>()
                .eq("user_id", getProfileId())
                .orderByDesc("created")
        );

        // 收藏文章数量
        int collectCount = postService.count(new QueryWrapper<Post>()
                .inSql("id", "SELECT post_id FROM user_collection WHERE user_id = " + getProfileId())
        );

        req.setAttribute("publishCount",publishCount);
        req.setAttribute("collectCount",collectCount);

        return "/user/index";
    }

    // 用户中心， 发表过的文章
    @ResponseBody
    @GetMapping("/user/publish")
    public Result userPublish() {
        // 查询id 和 shiro缓存的 id 相同的用户博客 ， 设置排序
        IPage page = postService.page(getPage(), new QueryWrapper<Post>()
                .eq("user_id", getProfileId())
                .orderByDesc("created")
        );

        return Result.success(page);
    }

    // 用户中心， 收藏过的文章
    @ResponseBody
    @GetMapping("/user/collect")
    public Result userCollect() {
        /**
         *     inSql  相当于  id in (...)   这一段内容
         *
         *     SELECT * FROM `post`
         *     where id in (
         *         select `post_id` from `user_collection`
         *         where `user_id` = 6
         *     )
         *     LIMIT 0,2
         */
        IPage page = postService.page(getPage(), new QueryWrapper<Post>()
                .inSql("id", "SELECT post_id FROM user_collection WHERE user_id = "+getProfileId())
                .orderByDesc("created")
        );


        return Result.success(page);
    }

    // 跳到我的消息
    @GetMapping("/user/message")
    public String message() {

        IPage<UserMessageVO> page = userMessageService.paging(getPagePlus(), new QueryWrapper<UserMessage>()
                .eq("to_user_id", getProfileId())  // 接收消息用户id
                .orderByDesc("created")
        );

        // 把消息改为已读状态
        List<Long> ids = new ArrayList<>();
        for (UserMessageVO messageVO : page.getRecords()) {
            // 把未读状态值 0[未读] 给修改掉
            if (messageVO.getStatus() == 0) {
                ids.add(messageVO.getId());
            }
        }

        // 批量修改 status
        userMessageService.updateStatusToReaded(ids);

        req.setAttribute("messagePageData", page);

        return "/user/message";
    }

    // 删除、清空消息
    @ResponseBody
    @PostMapping("/message/remove/")
    public Result msgRemove(Long id,@RequestParam(defaultValue = "false") Boolean all) {

        boolean res = userMessageService.remove(new QueryWrapper<UserMessage>()
                .eq("to_user_id", getProfileId())
                .eq(!all, "id", id)
        );

        return res ? Result.success().action("/user/message") : Result.fail("删除失败");
    }

    // 未读消息提醒
    @ResponseBody
    @RequestMapping("/message/nums/")
    public Map msgNums() {

        int count = userMessageService.count(new QueryWrapper<UserMessage>()
                .eq("to_user_id", getProfileId())
                .eq("status", "0")
        );

        return MapUtil
                .builder("status",0)
                .put("count", count)
                .build();
    }

}

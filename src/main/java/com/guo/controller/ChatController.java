package com.guo.controller;

import cn.hutool.core.map.MapUtil;
import com.guo.common.lang.Consts;
import com.guo.common.lang.Result;
import com.guo.im.vo.ImUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController extends BaseController{

    @GetMapping("/getMineAndGroupData")
    public Result getMineAndGroupData() {

        // 默认群
        Map<String, Object> group = new HashMap<>();
        group.put("name", "社区群聊");
        group.put("type", "group");
        group.put("avatar", "http://pic.90sjimg.com/design/00/67/59/63/58e8ebdd5c471.png");
        group.put("id", Consts.IM_GROUP_ID);
        group.put("members", 0);

        ImUser imUser = chatService.getCurrentUser();

        return Result.success(MapUtil
                .builder()
                .put("group",group)
                .put("mine", imUser)
                .map());
    }

    // 历史记录
    @GetMapping("/getGroupHistoryMsg")
    public Result getGroupHistoryMsg() {
        List<Object> historyMsg = chatService.getGroupHistoryMsg(500);
        return Result.success(historyMsg);
    }

}

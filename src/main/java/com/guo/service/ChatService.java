package com.guo.service;

import com.guo.im.vo.ImMess;
import com.guo.im.vo.ImUser;

import java.util.List;

public interface ChatService {
    ImUser getCurrentUser();

    public void setGroupHistoryMsg(ImMess imMess);

    public List<Object> getGroupHistoryMsg(int count);
}

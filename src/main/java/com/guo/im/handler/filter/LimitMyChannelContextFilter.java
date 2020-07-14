package com.guo.im.handler.filter;

import lombok.Data;
import org.tio.core.ChannelContext;
import org.tio.core.ChannelContextFilter;

@Data
public class LimitMyChannelContextFilter implements ChannelContextFilter {

    private ChannelContext currentContext;

    @Override
    public boolean filter(ChannelContext channelContext) {
        // 如果是自己就过滤掉
        if (currentContext.userid.equals(channelContext.userid)) {
            return false;
        }
        return true;
    }
}

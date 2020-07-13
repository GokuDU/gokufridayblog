package com.guo.im.vo;

import lombok.Data;

@Data
public class ImUser {

    public final static String ONLINE_STATUS = "online";
    public final static String HIDE_STATUS = "hide";

    private String username;
    private Long id;
    private String status;  // 在线状态 online：在线、hide：隐身
    private String sign;    // 我的签名
    private String avatar;

}

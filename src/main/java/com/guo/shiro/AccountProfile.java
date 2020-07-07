package com.guo.shiro;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *  登录用户通用属性
 */
@Data
public class AccountProfile implements Serializable {

    private Long id;
    private String username;
    private String email;
    private String avatar;
    private Date created;
    private String gender;

    // 将 gender 的 0、1 转换为对应的具体性别
    public String getSex() {
        return "0".equals(gender) ? "女" : "男";
    }

}

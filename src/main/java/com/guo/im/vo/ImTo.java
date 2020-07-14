package com.guo.im.vo;

import lombok.Data;

@Data
public class ImTo {

    private Long id;
    private String username;
    private String type;  //  "type":"group"
    private String members;
    private String avatar;
}

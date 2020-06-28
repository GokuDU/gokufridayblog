package com.guo.vo;

import com.guo.entity.Post;
import lombok.Data;

@Data
public class PostVO extends Post {
    private Long authorId;
    private String authorName;
    private String authorAvatar;

    private String categoryName;
}

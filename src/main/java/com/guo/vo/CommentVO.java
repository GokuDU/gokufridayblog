package com.guo.vo;

import com.guo.entity.Comment;
import lombok.Data;

@Data
public class CommentVO extends Comment {
    private Long authorId;
    private String authorName;
    private String authorAvatar;
}

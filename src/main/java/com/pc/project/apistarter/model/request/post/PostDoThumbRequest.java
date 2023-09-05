package com.pc.project.apistarter.model.request.post;

import lombok.Data;

import java.io.Serializable;

/**
 * 点赞 / 取消点赞请求
 *
 * @author pengchang
 */
@Data
public class PostDoThumbRequest implements Serializable {

    /**
     * 帖子 id
     */
    private long postId;

    private static final long serialVersionUID = 1L;
}
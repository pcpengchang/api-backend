package com.pc.project.apicommon.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pc.apicommon.model.entity.Post;
import com.pc.project.apistarter.model.request.post.PostQueryRequest;
import com.pc.project.apistarter.model.vo.PostVO;

/**
 * @author pengchang
 * @description 针对表【post(帖子)】的数据库操作Service
 */
public interface PostService extends IService<Post> {

    /**
     * 校验
     *
     * @param post
     * @param add 是否为创建校验
     */
    void validPost(Post post, boolean add);

    /**
     * 分页查询帖子
     * @param postQueryRequest
     * @return
     */
    Page<PostVO> listPostVOByPage(PostQueryRequest postQueryRequest);
}

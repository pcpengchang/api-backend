package com.pc.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pc.apicommon.model.entity.Post;

import java.util.Date;
import java.util.List;

/**
* @author pengchangli
* @description 针对表【post(帖子)】的数据库操作Mapper
* @createDate 2022-09-13 16:03:41
* @Entity com.pc.project.model.entity.Post
*/
public interface PostMapper extends BaseMapper<Post> {
    /**
     * 查询帖子列表（包括已被删除的数据）
     */
    List<Post> listPostWithDelete(Date minUpdateTime);
}





package com.pc.project.apidomain.post;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pc.apicommon.model.entity.Post;
import com.pc.project.apicommon.response.ErrorCode;
import com.pc.project.apicommon.service.PostService;
import com.pc.project.apiinfrastructure.mapper.PostMapper;
import com.pc.project.apistarter.exception.BusinessException;
import com.pc.project.apistarter.model.request.post.PostQueryRequest;
import com.pc.project.apistarter.model.vo.PostVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author pengchangli
 * @description 针对表【post(帖子)】的数据库操作Service实现
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Override
    public void validPost(Post post, boolean add) {
        if (post == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String content = post.getContent();
        // 创建时，所有参数必须非空
        if (add) {
            if (StringUtils.isAnyBlank(content)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }

    @Override
    public Page<PostVO> listPostVOByPage(PostQueryRequest postQueryRequest) {
        long current = postQueryRequest.getCurrent();
        long pageSize = postQueryRequest.getPageSize();

        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        String searchText = postQueryRequest.getSearchText();
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("title", searchText).or().like("content", searchText);
        }

        Page<Post> postPage = this.page(new Page<>(current, pageSize), queryWrapper);
        Page<PostVO> postVOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        List<Post> postList = postPage.getRecords();
        List<PostVO> postVOList = postList.stream().map(PostVO::objToVo).collect(Collectors.toList());
        postVOPage.setRecords(postVOList);

        return postVOPage;
    }
}





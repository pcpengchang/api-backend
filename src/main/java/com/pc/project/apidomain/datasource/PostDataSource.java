package com.pc.project.apidomain.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pc.project.apidomain.post.PostQueryRequest;
import com.pc.project.apistarter.model.vo.PostVO;
import com.pc.project.apicommon.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author pengchang
 * @date 2023/08/10 22:56
 **/
@Service
@Slf4j
public class PostDataSource implements DataSource<PostVO> {

    @Resource
    private PostService postService;

    @Override
    public Page<PostVO> doSearch(String searchText, long pageNum, long pageSize) {
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        postQueryRequest.setSearchText(searchText);
        postQueryRequest.setCurrent(pageNum);
        postQueryRequest.setPageSize(pageSize);
        return postService.listPostVOByPage(postQueryRequest);
    }
}

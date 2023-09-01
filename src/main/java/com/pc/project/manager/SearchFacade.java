package com.pc.project.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pc.project.common.ErrorCode;
import com.pc.project.datasource.DataSource;
import com.pc.project.datasource.DataSourceRegistry;
import com.pc.project.datasource.PictureDataSource;
import com.pc.project.datasource.PostDataSource;
import com.pc.project.exception.BusinessException;
import com.pc.project.model.dto.search.SearchRequest;
import com.pc.project.model.entity.Picture;
import com.pc.project.model.enums.SearchTypeEnum;
import com.pc.project.model.enums.SearchVO;
import com.pc.project.model.vo.PostVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

/**
 * 搜索门面
 */
@Component
@Slf4j
public class SearchFacade {
    @Resource
    private PictureDataSource pictureDataSource;

    @Resource
    private PostDataSource postDataSource;

    @Resource
    private DataSourceRegistry dataSourceRegistry;

    public SearchVO searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        // String type = searchRequest.getType();
        String type = "picture + post";
        if (StringUtils.isBlank(type)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取搜索类型 减少if-else冗余
        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);

        String searchText = searchRequest.getSearchText();
        long current = searchRequest.getCurrent();
        long pageSize = searchRequest.getPageSize();

        // 搜索出所有数据
        if (searchTypeEnum == null) {
//            CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> {
//                UserQueryRequest userQueryRequest = new UserQueryRequest();
//                userQueryRequest.setUserName(searchText);
//                Page<UserVO> userVOPage = userDataSource.doSearch(searchText, current, pageSize);
//                return userVOPage;
//            });
//
            CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> postDataSource.doSearch(searchText, current, pageSize));

            CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() -> pictureDataSource.doSearch(searchText, current, pageSize));

            CompletableFuture.allOf(postTask, pictureTask).join();
            // CompletableFuture.allOf(pictureTask).join();

            try {
//                Page<UserVO> userVOPage = userTask.get();
                Page<PostVO> postVOPage = postTask.get();
                Page<Picture> picturePage = pictureTask.get();
                SearchVO searchVO = new SearchVO();
//                searchVO.setUserList(userVOPage.getRecords());
                searchVO.setPostList(postVOPage.getRecords());
                searchVO.setPictureList(picturePage.getRecords());
                return searchVO;
            } catch (Exception e) {
                log.error("查询异常", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
            }
        } else {
            SearchVO searchVO = new SearchVO();
            DataSource<?> dataSource = dataSourceRegistry.getDataSourceByType(type);
            Page<?> page = dataSource.doSearch(searchText, current, pageSize);
            searchVO.setDataList(page.getRecords());
            return searchVO;
        }
    }
}

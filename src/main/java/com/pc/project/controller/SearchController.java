package com.pc.project.controller;

import com.pc.project.common.BaseResponse;
import com.pc.project.utils.ResultUtils;
import com.pc.project.manager.SearchFacade;
import com.pc.project.model.dto.search.SearchRequest;
import com.pc.project.model.enums.SearchVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author pengchang
 * @date 2023/03/31 11:19
 **/
@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {
    /**
     * 门面模式
     */
    @Resource
    private SearchFacade searchFacade;

    @GetMapping("/all")
    public BaseResponse<SearchVO> searchAll(SearchRequest searchRequest, HttpServletRequest request) {
        if (StringUtils.isEmpty(searchRequest.getSearchText())) {
            return ResultUtils.success(null);
        }
        return ResultUtils.success(searchFacade.searchAll(searchRequest, request));
    }
}

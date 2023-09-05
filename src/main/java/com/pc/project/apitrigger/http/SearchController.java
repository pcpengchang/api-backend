package com.pc.project.apitrigger.http;

import com.pc.project.apicommon.response.BaseResponse;
import com.pc.project.apistarter.model.request.datasource.SearchRequest;
import com.pc.project.apistarter.model.vo.SearchVO;
import com.pc.project.apistarter.service.SearchFacade;
import com.pc.project.apistarter.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

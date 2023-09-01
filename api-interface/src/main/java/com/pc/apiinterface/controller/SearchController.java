package com.pc.apiinterface.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.pc.apiclientsdk.model.SearchRequest;
import com.pc.apiinterface.common.BaseResponse;
import com.pc.apiinterface.common.ResultUtils;
import com.yupi.yucongming.dev.common.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author pengchang
 * @date 2023/08/02 22:03
 **/
@Slf4j
@RestController
@RequestMapping("/search")
public class SearchController {

    @PostMapping("/getTags")
    public BaseResponse<String> getTags(@RequestBody SearchRequest searchRequest) {
        if (StringUtils.isEmpty(searchRequest.getSearchText())) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        // 1. 获取数据
        String json = "{\"current\":1,\"pageSize\":8,\"sortField\":\"_score\",\"sortOrder\":\"descend\",\"searchText\":\"%s\",\"reviewStatus\":1}";

        json = String.format(json, searchRequest.getSearchText());
        System.out.println(json);
        String url = "https://www.code-nav.cn/api/post/search/page/vo";
        String result = HttpRequest
                .post(url)
                .body(json)
                .execute()
                .body();

        // 2. json 转对象
        Map<String, Object> map = JSONUtil.toBean(result, Map.class);
        JSONObject data = (JSONObject) map.get("data");
        JSONArray records = (JSONArray) data.get("records");

        HashSet<String> tags = new HashSet<>();
        for (Object record : records) {
            JSONObject tempRecord = (JSONObject) record;
            JSONArray objects = (JSONArray) tempRecord.get("tags");
            List<String> tagList = objects.toList(String.class);
            tags.addAll(tagList);
        }

        return ResultUtils.success(String.valueOf(tags));
    }

    @PostMapping("/translate")
    public BaseResponse<String> translate(@RequestBody SearchRequest searchRequest) {
        if (StringUtils.isEmpty(searchRequest.getSearchText())) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String url = String.format("http://api.btstu.cn/tst/api.php?text=%s", searchRequest.getSearchText());
        String result = HttpRequest
                .get(url)
                .header("text", searchRequest.getSearchText())
                .execute()
                .body();
        Map<String, Object> map = JSONUtil.toBean(result, Map.class);
        Object tst = map.get("tst");
        return ResultUtils.success(tst.toString());
    }

    @PostMapping("/getBaiduNews")
    public BaseResponse<String> getBaiduNews(@RequestBody SearchRequest searchRequest) {
        if (searchRequest.getSize() == null || searchRequest.getSize() == 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String baiduUrl = "https://www.coderutil.com/api/resou/v1/baidu";
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("access-key", "2a73055beafb826cf0aaf0d284d9eede");
        paramMap.put("secret-key", "3fe196bd0a439eef303155b3870b71d5");

        String result = HttpUtil.get(baiduUrl, paramMap);

        Map<String, Object> map = JSONUtil.toBean(result, Map.class);
        JSONArray data = (JSONArray) map.get("data");

        Map<String, String> tags = new HashMap<>();
        for (Object record : data) {
            JSONObject tempRecord = (JSONObject) record;
            String keyword = (String) tempRecord.get("keyword");
            String url = (String) tempRecord.get("url");
            tags.put(keyword, url);
        }

        return ResultUtils.success(tags.toString());
    }

    @PostMapping("/getWeatherInfo")
    public String getWeatherInfo(@RequestBody SearchRequest searchRequest, HttpServletRequest request) throws Exception {
        if (StringUtils.isEmpty(searchRequest.getSearchText())) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR).toString();
        }
        String weatherUrl = "https://api.vvhan.com/api/weather";
        HashMap<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("city", searchRequest.getSearchText());
        return HttpUtil.get(weatherUrl, paramMap);
    }

    /**
     * 网易云音乐热门评论
     */
    @PostMapping("/getHotComments")
    public String getHotComments(HttpServletRequest request) {
        String url = "https://api.uomg.com/api/comments.163";
        HttpResponse httpResponse = HttpRequest.get(url)
                .execute();
        return httpResponse.body();
    }

    /**
     * 沙雕语录
     */
    @PostMapping("/getInterestingText")
    public BaseResponse<String> getInterestingText(HttpServletRequest request) {
        String url = "https://tenapi.cn/v2/yiyan";
        HttpResponse httpResponse = HttpRequest.get(url)
                .execute();
        return ResultUtils.success(httpResponse.toString());
    }

    /**
     * 名人名言
     */
    @PostMapping("/getWisdomText")
    public String getWisdomText(HttpServletRequest request) {
        String url = "https://api.apiopen.top/api/sentences";
        HttpResponse httpResponse = HttpRequest.get(url)
                .execute();
        return httpResponse.body();
    }

    @PostMapping("/getCommonText")
    public BaseResponse<String> getCommonText(String url, HttpServletRequest request) {
        HttpResponse httpResponse = HttpRequest.get(url)
                .execute();
        return ResultUtils.success(httpResponse.toString());
    }
}

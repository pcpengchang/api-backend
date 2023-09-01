package com.pc.apiinterface.controller;

import com.pc.apiclientsdk.model.User;
import com.pc.apiinterface.common.BaseResponse;
import com.pc.apiinterface.common.ResultUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 名称 API
 *
 * @author pengchang
 */
@RestController
@RequestMapping("/name")
public class NameController {
    @PostMapping("/post")
    public BaseResponse<String> getNameByPost(@RequestParam String name) {
        return ResultUtils.success("POST 你的名字是" + name);
    }

    @PostMapping("/user")
    public BaseResponse<String> getUsernameByPost(@RequestBody User user) {
        return ResultUtils.success("示例接口：POST 用户名字是" + user.getUsername());
    }
}

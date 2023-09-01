package com.pc.apiinterface.controller;

import com.pc.apiclientsdk.model.AIRequest;
import com.pc.apiinterface.common.BaseResponse;
import com.pc.apiinterface.common.ResultUtils;
import com.pc.apiinterface.manager.AiManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author pengchang
 * @date 2023/08/02 21:16
 **/
@RestController
@RequestMapping("/ai")
public class AIController {

    /**
     * BI 模型 id
     */
    private static final long BI_MODEL_ID = 1685519303849390082L;

    @Resource
    private AiManager aiManager;

    @PostMapping("/chat")
    public BaseResponse<String> chat(@RequestBody AIRequest aiRequest) {
        return ResultUtils.success(aiManager.doChat(BI_MODEL_ID, aiRequest.getText()));
    }
}

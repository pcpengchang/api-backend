package com.pc.project.apitrigger.http;

import com.pc.project.apicommon.response.BaseResponse;
import com.pc.project.apicommon.service.GeneratorFacade;
import com.pc.project.apistarter.model.vo.GenerateVO;
import com.pc.project.apistarter.model.request.datagenerator.TableSchemaRequest;
import com.pc.project.apistarter.utils.ResultUtils;
import freemarker.template.TemplateException;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author pengchang
 * @date 2023/08/06 16:04
 **/
@RestController
@RequestMapping("/sql")
@Slf4j
public class SqlController {
    @Resource
    private GeneratorFacade generatorFacade;

    @ApiOperation(value = "输入表名、字段名和行数等")
    @PostMapping("/generate/schema")
    public BaseResponse<GenerateVO> generateBySchema(@RequestBody TableSchemaRequest tableSchema) throws TemplateException, IOException {
        // 智能导入（多字段填充构建）
        return ResultUtils.success(generatorFacade.generateAll(tableSchema));
    }


}

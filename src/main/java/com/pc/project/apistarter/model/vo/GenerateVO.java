package com.pc.project.apistarter.model.vo;

import com.pc.project.apistarter.model.request.datagenerator.TableSchemaRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author pengchang
 * @date 2023/08/06 16:05
 **/
@Data
public class GenerateVO implements Serializable {

    private TableSchemaRequest tableSchema;

    private String createSql;

    private List<Map<String, Object>> dataList;

    private String insertSql;

    private String dataJson;

    private String javaEntityCode;

    private String javaObjectCode;

    private static final long serialVersionUID = 7122637163626243606L;
}

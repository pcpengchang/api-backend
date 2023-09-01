package com.pc.apiclientsdk.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author pengchang
 * @date 2023/08/02 21:25
 **/
@Data
@Builder
public class Result {
    private int status;
    private String body;
}

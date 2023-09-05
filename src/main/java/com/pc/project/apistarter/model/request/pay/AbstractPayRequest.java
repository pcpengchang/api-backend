package com.pc.project.apistarter.model.request.pay;

import lombok.Data;

import java.io.Serializable;

/**
 * @author pengchang
 * @date 2023/09/01 15:25
 **/
@Data
public abstract class AbstractPayRequest implements Serializable {
    /**
     * 交易环境，H5、小程序、网站等
     */
    private Integer tradeType;

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 支付渠道
     */
    private Integer channel;

    /**
     * 商户订单号
     * 由商家自定义，64个字符以内，仅支持字母、数字、下划线且需保证在商户端不重复
     */
    private String orderRequestId;

    private static final long serialVersionUID = 1L;
}


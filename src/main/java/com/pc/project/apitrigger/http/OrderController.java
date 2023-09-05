package com.pc.project.apitrigger.http;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConstants;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.pc.project.apicommon.response.ErrorCode;
import com.pc.project.apistarter.exception.BusinessException;
import com.pc.project.apistarter.enums.PayChannelEnum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @author pengchang
 * @date 2023/08/17 11:32
 **/
@Slf4j
@Controller
public class OrderController {

    @Resource
    private AlipayClient alipayClient;

    @Resource
    private Environment config;

    @GetMapping("/trade/page/pay")
    public void tradePagePay(HttpServletResponse httpResponse) {

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(config.getProperty("alipay.notify-url"));
        request.setReturnUrl(config.getProperty("alipay.return-url"));
        // 订单总金额，单位为元，精确到小数点后两位
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setOutTradeNo(RandomUtil.randomNumbers(8));
        model.setTotalAmount(new BigDecimal("0.01").toString());
        model.setSubject("测试商品");
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        request.setBizModel(model);

        try {
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            if (!response.isSuccess()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            String form = response.getBody();
            httpResponse.setContentType("text/html;charset=" + AlipayConstants.CHARSET_UTF8);
            httpResponse.getWriter().write(form);
            httpResponse.getWriter().flush();
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
    }

    @ResponseBody
    @PostMapping("/trade/notify")
    public String tradeNotify(@RequestParam Map<String, String> params) {
        // 将异步通知中收到的所有参数都存放到map中
        log.info("通知参数 ===> {}", params);
        PayCallbackCommand payCallbackCommand = BeanUtil.mapToBean(params, PayCallbackCommand.class,
                true, CopyOptions.create());
        payCallbackCommand.setChannel(PayChannelEnum.ALI_PAY.getCode());
        payCallbackCommand.setOrderRequestId(params.get("out_trade_no"));
        payCallbackCommand.setGmtPayment(DateUtil.parse(params.get("gmt_payment")));
        return payCallbackCommand.toString();
    }
}

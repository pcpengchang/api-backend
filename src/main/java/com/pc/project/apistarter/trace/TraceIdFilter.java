package com.pc.project.apistarter.trace;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 链路追踪过滤器
 *
 * @author: ChickenWing
 * @date: 2023/1/26
 */
@Component
@Slf4j
public class TraceIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        String traceId = request.getHeader(TraceIdConstant.TRACE_ID);
        if (StringUtils.isBlank(traceId)) {
            traceId = TraceIdContext.generateTraceId();
        }
        TraceIdContext.setTraceId(traceId);
        filterChain.doFilter(request, resp);
        TraceIdContext.clearTraceId();
    }

}
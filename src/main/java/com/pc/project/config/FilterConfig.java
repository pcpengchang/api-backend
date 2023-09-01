package com.pc.project.config;

import com.pc.project.trace.TraceIdFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Filter过滤器配置
 */
@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<TraceIdFilter> registerTraceFilter() {
        FilterRegistrationBean<TraceIdFilter> filterRegBean = new FilterRegistrationBean<>();
        filterRegBean.setFilter(new TraceIdFilter());
        filterRegBean.addUrlPatterns("/*");
        filterRegBean.setOrder(5);
        return filterRegBean;
    }
}

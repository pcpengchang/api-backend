//package com.pc.apiinterface.filter;
//
//import org.springframework.stereotype.Component;
//
//import javax.servlet.*;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.http.HttpServletRequest;
//import java.io.IOException;
//
//@Component
//@WebFilter(filterName = "BaseFilter", urlPatterns = {"/api/**"})
//public class BaseFilter implements Filter {
//
//    private static final String GATEWAY_KEY = "source";
//    private static final String GATEWAY_VALUE = "pengchang";
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        Filter.super.init(filterConfig);
//    }
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest)servletRequest;
//        String gateway = request.getHeader(GATEWAY_KEY);
//        if (!GATEWAY_VALUE.equals(gateway)) {
//            return;
//        }
//        filterChain.doFilter(servletRequest, servletResponse);
//    }
//
//    @Override
//    public void destroy() {
//        Filter.super.destroy();
//    }
//}

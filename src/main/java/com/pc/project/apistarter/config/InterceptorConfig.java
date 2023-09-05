package com.pc.project.apistarter.config;

import com.pc.project.apistarter.interceptor.EncryptInterceptor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author pengchang
 * @date 2023/08/28 13:39
 **/
@Configuration
public class InterceptorConfig {

    public static final String traceExecution = "execution(* com.pc.*.controller.*Controller.*(..))";

    @Bean
    public DefaultPointcutAdvisor defaultPointcutAdvisor() {
        EncryptInterceptor interceptor = new EncryptInterceptor();
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(traceExecution);
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(pointcut);
        advisor.setAdvice(interceptor);
        return advisor;
    }

}
package com.pc.project.apistarter.aop;

import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * todo 修改切面位置
 *
 * @author pengchang
 * @date 2023/08/28 14:06
 **/
@Aspect
@Slf4j
@Component
@ConditionalOnProperty(name = {"serviceLog.aspect.enable"}, havingValue = "true", matchIfMissing = true)
public class ServiceLogAspect {

    @Pointcut("execution(* com.pc..*ServiceImpl.*(..))")
    private void pointCut() {
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Object[] reqArgs = pjp.getArgs();
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        String methodName = methodSignature.getDeclaringType().getName() + "." + methodSignature.getName();
        log.info("{},req:{}", methodName, reqArgs);
        Stopwatch stopwatch = Stopwatch.createStarted();
        Object responseObj = pjp.proceed();
        String resp = new Gson().toJson(responseObj);
        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        log.info("{},response:{},costTime:{}", methodName, resp, elapsed);
        return responseObj;
    }

}

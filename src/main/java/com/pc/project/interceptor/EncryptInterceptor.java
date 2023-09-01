package com.pc.project.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.pc.project.annotation.ResultEncrypt;
import com.pc.project.common.BaseResponse;
import com.pc.project.utils.EncodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.support.DefaultPointcutAdvisor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

@Slf4j
public class EncryptInterceptor extends DefaultPointcutAdvisor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Method targetMethod = methodInvocation.getMethod();
        Object result = methodInvocation.proceed();
        ResultEncrypt resultEncrypt = targetMethod.getAnnotation(ResultEncrypt.class);
        if (resultEncrypt != null) {
            result = encryptResult(targetMethod, result, resultEncrypt.value());
        }
        return result;
    }

    public static Object encryptResult(Method targetMethod, Object result, String[] values) {
        try {
            return doEncryptResult(result, values);
        } catch (Exception e) {
            log.error("EncryptInterceptor.encryptResult.error:{}", e.getMessage(), e);
            return result;
        }
    }

    public static Object doEncryptResult(Object result, String[] values) throws NoSuchFieldException, IllegalAccessException {
        if (values == null) {
            return result;
        }
        Object encryptData;
        if (result instanceof BaseResponse) {
            encryptData = ((BaseResponse<?>) result).getData();
        } else {
            Field dataField = result.getClass().getDeclaredField("data");
            dataField.setAccessible(true);
            encryptData = dataField.get(result);
        }
        for (String path : values) {
            try {
                int index = StringUtils.lastIndexOf(path, ".");
                String expressPrefix = StringUtils.substring(path, 0, index);
                String fieldName = StringUtils.substring(path, index + 1);
                Object prefixObj = JSONPath.eval(encryptData, expressPrefix);
                int size = -1;
                if (prefixObj instanceof Collection) {
                    size = ((Collection<?>) prefixObj).size();
                }
                if (size == -1) {
                    //单个对象
                    Object data = JSONPath.eval(encryptData, path);
                    if (data == null) {
                        continue;
                    }
                    JSONPath.set(encryptData, path, EncodeUtils.encode(String.valueOf(data)));
                } else {
                    //集合
                    for (int i = 0; i < size; i++) {
                        Object oldData = JSONPath.eval(encryptData, expressPrefix + "[" + i + "]." + fieldName);
                        if (oldData == null) {
                            continue;
                        }
                        JSONPath.set(encryptData, expressPrefix + "[" + i + "]." + fieldName,
                                EncodeUtils.encode(String.valueOf(oldData)));
                    }
                }
            } catch (Exception e) {
                log.error("EncryptInterceptor.doEncryptResult.error:{}", e.getMessage(), e);
            }
        }
        if (result instanceof BaseResponse) {
            ((BaseResponse) result).setData(encryptData);
        } else {
            Field dataFiled = result.getClass().getDeclaredField("data");
            dataFiled.setAccessible(true);
            dataFiled.set(result, encryptData);
        }
        if (log.isInfoEnabled()) {
            log.info("doEncryptResult dataFiled done, result:{}, encryptData:{}", JSON.toJSONString(result),
                    JSON.toJSONString(encryptData));
        }
        return result;
    }

}
package com.pc.gateway;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.pc.apicommon.model.entity.InterfaceInfo;
import com.pc.apicommon.model.entity.User;
import com.pc.apicommon.service.IInterfaceInfoService;
import com.pc.apicommon.service.IUserInterfaceInfoService;
import com.pc.apicommon.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * @author pengchang
 * @date 2023/09/08 22:03
 **/
@Slf4j
@Service
public class RpcCommandManger {
    @DubboReference
    private IUserService innerUserService;

    @DubboReference
    private IInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private IUserInterfaceInfoService innerUserInterfaceInfoService;

    @HystrixCommand(
            fallbackMethod = "defaultGetInvokeUser",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
                            value = "20000"),
            },
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "30")
            }
    )
    public User getInvokeUser(String accessKey) {
        try {
            return innerUserService.getInvokeUser(accessKey);
        } catch (Exception e) {
            log.error("RpcCommandManger getInvokeUser error", e);
            return null;
        }
    }
    public User defaultGetInvokeUser(String accessKey) {
        log.error("RpcCommandManger.getInvokeUser fallback");
        return null;
    }

    @HystrixCommand(
            fallbackMethod = "defaultGetInterfaceInfo",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
                            value = "20000"),
            },
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "30")
            }
    )
    public InterfaceInfo getInterfaceInfo(String path) {
        try {
            return innerInterfaceInfoService.getInterfaceInfo(path);
        } catch (Exception e) {
            log.error("RpcCommandManger getInterfaceInfo error", e);
            return null;
        }
    }
    public InterfaceInfo defaultGetInterfaceInfo(String path) {
        log.error("RpcCommandManger.getInterfaceInfo fallback");
        return null;
    }

    @HystrixCommand(
            fallbackMethod = "defaultReduceInvokeCount",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
                            value = "20000"),
            },
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "30")
            }
    )
    public void reduceInvokeCount(long interfaceInfoId, long userId) {
        try {
            innerUserInterfaceInfoService.reduceInvokeCount(interfaceInfoId, userId);
        } catch (Exception e) {
            log.error("RpcCommandManger reduceInvokeCount error", e);
            throw new RuntimeException(e);
        }
    }
    public void defaultReduceInvokeCount(long interfaceInfoId, long userId) {
        log.error("RpcCommandManger.reduceInvokeCount fallback");
        throw new RuntimeException();
    }

    @HystrixCommand(
            fallbackMethod = "defaultRollbackInvokeCount",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
                            value = "20000"),
            },
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "30")
            }
    )
    public void rollbackInvokeCount(long interfaceInfoId, long userId) {
        try {
            innerUserInterfaceInfoService.rollbackInvokeCount(interfaceInfoId, userId);
        } catch (Exception e) {
            log.error("RpcCommandManger rollbackInvokeCount error", e);
            throw new RuntimeException(e);
        }
    }
    public void defaultRollbackInvokeCount(long interfaceInfoId, long userId) {
        log.error("RpcCommandManger.rollbackInvokeCount fallback");
        throw new RuntimeException();
    }
}

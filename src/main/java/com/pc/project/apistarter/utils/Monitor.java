package com.pc.project.apistarter.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Monitor {
    // todo 接入邮箱，飞书告警

    public void runWithMonitorVoid(VoidAction voidAction, String actionName, boolean sendError) {
        try {
            voidAction.doIt();
        } catch (Exception e) {
            //仅记录关键信息，不打印栈
            log.error("runWithMonitorVoid error, actionName={}, exception={}", actionName,
                    e.getClass().getSimpleName());
            //发送消息
            if (sendError) {
                sendMsg(actionName, e);
            }
            throw e;
        }
    }

    public <T> T runWithMonitor(ReturnedAction<T> returnedAction, String actionName, boolean sendError) {
        try {
            return returnedAction.doIt();
        } catch (Exception e) {
            //仅记录关键信息，不打印栈
            log.error("runWithMonitorVoid error, actionName={}, exception={}", actionName,
                    e.getClass().getSimpleName());
            //发送消息
            if (sendError) {
                sendMsg(actionName, e);
            }
            throw e;
        }
    }

    private void sendMsg(String actionName, Exception e) {
        String errorMsg = String.format("biz run error, action:%s\nexception:%s\nsummary:%s", actionName,
                e.getClass().getSimpleName(), StringUtils.substring(e.getMessage(), 0, 200));

    }


    public interface VoidAction {
        void doIt();
    }

    public interface ReturnedAction<T> {
        T doIt();
    }

}

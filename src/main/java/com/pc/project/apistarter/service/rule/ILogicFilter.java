package com.pc.project.apistarter.service.rule;

import com.pc.apicommon.model.entity.User;

/**
 * 规则过滤
 *
 * @author pengchang
 * @date 2023/09/16 21:20
 **/
public interface ILogicFilter {
    void filter(User user);
}

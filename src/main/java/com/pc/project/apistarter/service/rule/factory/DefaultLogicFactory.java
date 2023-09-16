package com.pc.project.apistarter.service.rule.factory;

import com.pc.apicommon.model.entity.User;
import com.pc.project.apistarter.service.rule.ILogicFilter;
import lombok.Data;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author pengchang
 * @date 2023/09/16 21:28
 **/
@Data
@Service
public class DefaultLogicFactory {
    @Resource
    private List<ILogicFilter> logicFilterList;

//    public DefaultLogicFactory(List<ILogicFilter> logicFilters) {
//        logicFilters.forEach(logic -> {
//            LogicStrategy strategy = AnnotationUtils.findAnnotation(logic.getClass(), LogicStrategy.class);
//            if (null != strategy) {
//                logicFilterMap.put(strategy.logicMode().getCode(), logic);
//            }
//        });
//    }

    public void doCheck(User user) {
        logicFilterList.forEach(item -> item.filter(user));
    };
}

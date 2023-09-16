package com.pc.project.apistarter.annotation;

import com.pc.project.apistarter.enums.LogicModelEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author pengchang
 * @date 2023/09/16 21:30
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogicStrategy {

    LogicModelEnum logicMode();

}

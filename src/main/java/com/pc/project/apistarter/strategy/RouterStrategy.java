package com.pc.project.apistarter.strategy;

/**
 * 策略路由接口
 */
public interface RouterStrategy<T> {
    /**
     * 是否需要执行
     *
     * @param param
     * @return
     */
    boolean needProcess(T param);
}

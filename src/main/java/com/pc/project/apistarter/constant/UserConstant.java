package com.pc.project.apistarter.constant;

/**
 * 用户常量
 *
 * @author pengchang
 */
public interface UserConstant {

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "userLoginState";

    /**
     * 用户签到
     */
    String USER_SIGN = "userSign";

    /**
     * 用户注册锁，Key Prefix + 用户名
     */
    String LOCK_USER_REGISTER = "user-service:lock:user-register:";

    /**
     * 用户已注册用户名，Key Prefix + 用户名
     */
    String USER_REGISTER_USE = "user-service:user-use:";

    /**
     * 系统用户 id（虚拟用户）
     */
    long SYSTEM_USER_ID = 0;

    //  region 权限

    /**
     * 默认权限
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员权限
     */
    String ADMIN_ROLE = "admin";

    // endregion
}

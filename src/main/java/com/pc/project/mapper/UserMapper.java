package com.pc.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pc.apicommon.model.entity.User;

/**
 * @Entity com.pc.project.model.domain.User
 */
public interface UserMapper extends BaseMapper<User> {

    User queryUserById(int id);

    void insertUser(User user);
}





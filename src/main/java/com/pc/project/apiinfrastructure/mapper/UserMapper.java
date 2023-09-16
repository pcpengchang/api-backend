package com.pc.project.apiinfrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pc.apicommon.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    User queryUserById(int id);

    void insertUser(User user);
}





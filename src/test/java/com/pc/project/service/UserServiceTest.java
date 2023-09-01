package com.pc.project.service;

import cn.hutool.core.util.BooleanUtil;
import com.pc.apicommon.model.entity.User;
import com.pc.project.annotation.AuthCheck;
import com.pc.project.constant.UserConstant;
import com.pc.project.mapper.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.pc.project.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务测试
 *
 * @author pengchang
 */
@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Resource
    private UserMapper userMapper;

    @Test
    void listUser() {
        System.out.println(userService.list());
    }

    @Test
    void testAddUser() {
        User user = new User();
        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        boolean result = userService.updateById(user);
        Assertions.assertTrue(result);
    }

    @Test
    void testDeleteUser() {
        boolean result = userService.removeById(1L);
        Assertions.assertTrue(result);
    }

    @Test
    void testGetUser() {
        User user = userService.getById(1L);
        Assertions.assertNotNull(user);
    }

    @Test
    void userRegister() {

    }

    @Test
    void queryUserById() {
        System.out.println(userMapper.queryUserById(1));
    }

    @Test
    void insertUser() {
        User user = new User();
        user.setId(5L);
        user.setUserAccount("1242334563432");
        user.setUserRole("user");
        user.setUserPassword("f8de235116ca2ec0b8ee885b5c743072");
        user.setSecretKey("89");
        user.setAccessKey("89");
        user.setIsDelete(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        userMapper.insertUser(user);
    }

    @Test
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    void testAop() {
        System.out.println("testAop");
    }

    @Resource
    private RedisService redisService;

    @Test
    void testRedis() {
        User user = new User();
        user.setId(5L);
        user.setUserAccount("1242334563432");
        user.setUserRole("user");
        user.setUserPassword("f8de235116ca2ec0b8ee885b5c743072");
        user.setSecretKey("89");
        user.setAccessKey("89");
        user.setIsDelete(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        redisService.set(USER_LOGIN_STATE, user);
        System.out.println(redisService.get(USER_LOGIN_STATE));
    }

    /**
     * 签到测试
     */
    @Test
    void Sign() {
        long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyy"));
        String key = "userSign" + userId + keySuffix;
        int dayOfMonth = now.getDayOfMonth();
        redisService.bitAdd(key, dayOfMonth - 1, true);
        redisService.bitAdd(key, dayOfMonth - 2, true);
        redisService.bitAdd(key, dayOfMonth - 3, true);

        // 获取本月截止今天为止的所有的签到记录，十进制
        List<Long> result = redisService.bitField(key, dayOfMonth, 0);
        System.out.println(result.get(0));
    }

    /**
     * UV测试
     */
    @Test
    void testUV() {
        //准备数组，装用户数据
        String[] users = new String[1000];
        int index = 0;
        for (int i = 1; i <= 1000000; i++) {
            users[index++] = "user_" + i;
            if (i % 1000 == 0) {
                index = 0;
                redisService.hyperAdd("h1l1", users);
            }
        }
        System.out.println(redisService.hyperGet("h1l1"));
    }

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 排行榜
     */
    @Test
    void queryLikes() {
        String key = "BLOG_LIKED_KEY" + 122;
        Boolean isMember = redisService.sIsMember(key, "123");
        if (BooleanUtil.isFalse(isMember)) {
            redisTemplate.opsForZSet().add(key, "123", System.currentTimeMillis());
        } else {

        }
        Set<Object> range = redisTemplate.opsForZSet().range(key, 0, 4);

        System.out.println(range);
    }

    @Test
    void testSetNx() {
        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent("12", "12");
        System.out.println(aBoolean);
        aBoolean = redisTemplate.opsForValue().setIfAbsent("12", "12");
        System.out.println(aBoolean);
        aBoolean = redisTemplate.opsForValue().setIfAbsent("12", "12");
        System.out.println(aBoolean);
    }
}
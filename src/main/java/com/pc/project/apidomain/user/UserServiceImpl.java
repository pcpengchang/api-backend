package com.pc.project.apidomain.user;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pc.apicommon.model.entity.User;
import com.pc.project.apicommon.response.ErrorCode;
import com.pc.project.apicommon.service.UserService;
import com.pc.project.apiinfrastructure.mapper.UserMapper;
import com.pc.project.apistarter.exception.BusinessException;
import com.pc.project.apistarter.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.pc.project.apistarter.constant.UserConstant.*;


/**
 * 用户服务实现类
 *
 * @author pengchang
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    @Resource
    private RedissonClient redissonClient;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisService redisService;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "yupi";

    private static final long USER_EXPIRE_TIME = 60 * 60 * 6;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER + userAccount);
        if (!lock.tryLock()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        try {
            // 账户不能重复
            if (hasUserAccount(userAccount)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 分配 accessKey, secretKey
            String accessKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(5));
            String secretKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(8));
            // 4. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        } finally {
            lock.unlock();
        }
    }

    private Boolean hasUserAccount(String userAccount) {
        Boolean hasUserAccount = redisService.sIsMember(USER_REGISTER_USE, userAccount);
        if (!hasUserAccount) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                redisService.sAdd(USER_REGISTER_USE, userAccount);
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public User userLogin(String code, HttpServletRequest request) {
        if (StringUtils.isBlank(code)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        User user = (User) redisService.get(code);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "发送’登录‘可重获动态码");
        }
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return user;
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        if (!hasUserAccount(userAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不存在");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return user;
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && ADMIN_ROLE.equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUserName(originUser.getUserName());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setAccessKey(originUser.getAccessKey());
        safetyUser.setSecretKey(originUser.getSecretKey());
        safetyUser.setIsDelete(originUser.getIsDelete());
        return safetyUser;
    }

    @Override
    public boolean updateSecretKey(Long id) {
        User user = this.getById(id);
        // 用户不存在
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        String accessKey = DigestUtil.md5Hex(SALT + user.getUserAccount() + RandomUtil.randomNumbers(5));
        String secretKey = DigestUtil.md5Hex(SALT + user.getUserAccount() + RandomUtil.randomNumbers(8));
        user.setSecretKey(secretKey);
        user.setAccessKey(accessKey);
        return this.updateById(user);
    }

    @Override
    public boolean sign(Long id) {
        User user = this.getById(id);
        // 用户不存在
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        LocalDateTime now = LocalDateTime.now();
        String key = USER_SIGN + id + now.format(DateTimeFormatter.ofPattern(":yyyy"));
        int dayOfMonth = now.getDayOfMonth();
        return redisService.bitAdd(key, dayOfMonth - 1, true);
    }

    @Override
    public long getSignCount(Long id) {
        User user = this.getById(id);
        // 用户不存在
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        LocalDateTime now = LocalDateTime.now();
        String key = USER_SIGN + id + now.format(DateTimeFormatter.ofPattern(":yyyy"));
        int dayOfMonth = now.getDayOfMonth();
        // 获取本月截止今天为止的所有的签到记录，十进制
        List<Long> signRecord = redisService.bitField(key, dayOfMonth, 0);

        if (CollectionUtils.isEmpty(signRecord) || signRecord.get(0) == 0) {
            return 0;
        }
        long num = signRecord.get(0), signCount = 0;
        while (num != 0) {
            num &= num - 1;
            signCount++;
        }
        return signCount;
    }

    public static void main(String[] args) {
        System.out.println(DigestUtils.md5DigestAsHex((SALT + "12345678").getBytes()));
    }
}





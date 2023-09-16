package com.pc.project.apitrigger.http;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.rholder.retry.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pc.apicommon.model.entity.InterfaceInfo;
import com.pc.apicommon.model.entity.User;
import com.pc.project.apicommon.request.DeleteRequest;
import com.pc.project.apicommon.request.IdRequest;
import com.pc.project.apicommon.response.BaseResponse;
import com.pc.project.apicommon.response.ErrorCode;
import com.pc.project.apicommon.service.InterfaceInfoService;
import com.pc.project.apicommon.service.UserService;
import com.pc.project.apistarter.annotation.AuthCheck;
import com.pc.project.apistarter.constant.CommonConstant;
import com.pc.project.apistarter.enums.InterfaceInfoStatusEnum;
import com.pc.project.apistarter.exception.BusinessException;
import com.pc.project.apistarter.handler.InterfaceInfoStateMachine;
import com.pc.project.apistarter.model.dto.InterfaceInfoDTO;
import com.pc.project.apistarter.model.request.interinfoinfo.InterfaceInfoAddRequest;
import com.pc.project.apistarter.model.request.interinfoinfo.InterfaceInfoInvokeRequest;
import com.pc.project.apistarter.model.request.interinfoinfo.InterfaceInfoQueryRequest;
import com.pc.project.apistarter.model.request.interinfoinfo.InterfaceInfoUpdateRequest;
import com.pc.project.apistarter.service.rule.factory.DefaultLogicFactory;
import com.pc.project.apistarter.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.pc.project.apistarter.cache.MemoryCacheManager.*;

/**
 * 接口管理
 *
 * @author pengchang
 */
@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterfaceInfoController {

    private static final String SDK_PACKAGE = "com.pc.apiclientsdk.client.";

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private DefaultLogicFactory logicFactory;

    private final boolean isAsync = false;

    // IO 型线程池
    private final ExecutorService ioExecutorService = new ThreadPoolExecutor(4, 20, 10, TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(10000));

    // region 增删改查

    /**
     * 创建
     *
     * @param interfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        // 校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        boolean result = interfaceInfoService.save(interfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        long newInterfaceInfoId = interfaceInfo.getId();
        BASE_INFO_CACHE.put(newInterfaceInfoId, Optional.ofNullable(interfaceInfo));
        return ResultUtils.success(newInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceInfoService.removeById(id);
        BASE_INFO_CACHE.remove(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param interfaceInfoUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest,
                                                     HttpServletRequest request) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        User user = userService.getLoginUser(request);
        long id = interfaceInfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = interfaceInfoService.updateById(interfaceInfo);

        //异步写
        if (isAsync) {
            ioExecutorService.submit((() -> updateBaseInfo(id, interfaceInfo)));
        } else {
            updateBaseInfo(id, interfaceInfo);
        }
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<InterfaceInfoDTO> getInterfaceInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 目前只走缓存
        InterfaceInfo interfaceInfo = getInterfaceInfoCacheById(id);
        InterfaceInfoDTO interfaceInfoDTO = new InterfaceInfoDTO();
        BeanUtils.copyProperties(interfaceInfo, interfaceInfoDTO);

        List<String> similarInterfaceInfos = interfaceInfoService.matchInterfaceInfos(5, interfaceInfo);
        interfaceInfoDTO.setSimilarInterfaceInfos(similarInterfaceInfos.toString());
        // InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        return ResultUtils.success(interfaceInfoDTO);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<InterfaceInfo>> listInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        if (interfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        List<InterfaceInfo> interfaceInfoList = interfaceInfoService.list(queryWrapper);
        return ResultUtils.success(interfaceInfoList);
    }

    /**
     * 分页获取列表
     * todo 改写为sql " > xxx limit xxx" 用id反查缓存
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest, HttpServletRequest request) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        String description = interfaceInfoQuery.getDescription();
        // description 需支持模糊搜索
        interfaceInfoQuery.setDescription(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        if (StringUtils.isNotBlank(description) || StringUtils.isNotBlank(sortField)) {
            QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
            queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
            queryWrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
            Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size), queryWrapper);
            return ResultUtils.success(interfaceInfoPage);
        }

        List<InterfaceInfo> interfaceInfoList = Lists.newArrayListWithExpectedSize((int) current);
        Page<InterfaceInfo> interfaceInfoPage = new Page<>();

        AtomicInteger cnt = new AtomicInteger(0);

        BASE_INFO_CACHE.forEach((k, v) -> {
                    InterfaceInfo info = getInterfaceInfoCacheById(k);
                    // if (info.getStatus() == InterfaceInfoStatusEnum.ONLINE.getValue()) {
                    cnt.getAndIncrement();
                    if (current == 1 && cnt.get() <= size) {
                        interfaceInfoList.add(info);
                    } else {
                        if (((current - 1) * size) + 1 <= cnt.get() && cnt.get() <= current * size) {
                            interfaceInfoList.add(info);
                        }
                    }
                }
        );

        interfaceInfoPage.setRecords(interfaceInfoList);
        interfaceInfoPage.setTotal(BASE_INFO_CACHE.size());
        return ResultUtils.success(interfaceInfoPage);
    }

    // endregion

    /**
     * 发布
     *
     * @param idRequest
     * @param request
     * @return
     */
    @PostMapping("/online")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest,
                                                     HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = idRequest.getId();

        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        // 判断是否存在
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 状态机判断是否允许做状态流转
        InterfaceInfoStatusEnum toStatus = InterfaceInfoStatusEnum.ONLINE;
        InterfaceInfoStatusEnum fromStatus = InterfaceInfoStatusEnum.findByCode(oldInterfaceInfo.getStatus());
        if (!InterfaceInfoStateMachine.isValid(fromStatus, toStatus)) {
            throw new BusinessException(ErrorCode.STATUS_UPDATE_ERROR);
        }

        // TODO 判断该接口是否可以调用
//        com.pc.apiclientsdk.model.User user = new com.pc.apiclientsdk.model.User();
//        user.setUsername("test");
//
//        String username = apiClient.getUsernameByPost(user, Math.toIntExact(oldInterfaceInfo.getId()));
//        if (StringUtils.isBlank(username)) {
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口验证失败");
//        }
        // 仅本人或管理员可修改
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(toStatus.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);

        // 异步写
        oldInterfaceInfo.setStatus(toStatus.getValue());
        if (isAsync) {
            ioExecutorService.submit((() -> updateBaseInfo(id, oldInterfaceInfo)));
        } else {
            updateBaseInfo(id, oldInterfaceInfo);
        }
        return ResultUtils.success(result);
    }

    /**
     * 下线
     *
     * @param idRequest
     * @param request
     * @return
     */
    @PostMapping("/offline")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody IdRequest idRequest,
                                                      HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = idRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 状态机判断是否允许做状态流转
        InterfaceInfoStatusEnum toStatus = InterfaceInfoStatusEnum.OFFLINE;
        InterfaceInfoStatusEnum fromStatus = InterfaceInfoStatusEnum.findByCode(oldInterfaceInfo.getStatus());
        if (!InterfaceInfoStateMachine.isValid(fromStatus, toStatus)) {
            throw new BusinessException(ErrorCode.STATUS_UPDATE_ERROR);
        }

        // 仅本人或管理员可修改
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(toStatus.getValue());

        boolean result = interfaceInfoService.updateById(interfaceInfo);

        //异步写
        oldInterfaceInfo.setStatus(toStatus.getValue());
        if (isAsync) {
            ioExecutorService.submit((() -> updateBaseInfo(id, oldInterfaceInfo)));
        } else {
            updateBaseInfo(id, oldInterfaceInfo);
        }
        return ResultUtils.success(result);
    }

    /**
     * 调用（核心）
     *
     * @param interfaceInfoInvokeRequest
     * @param request
     * @return
     */
    @PostMapping("/invoke")
    public BaseResponse<Object> invokeInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest
                                                            interfaceInfoInvokeRequest,
                                                    HttpServletRequest request) {
        // 前置规则过滤
        User loginUser = userService.getLoginUser(request);
        // redisLimiterManager.doRateLimit("invoke_" + loginUser.getId());
        logicFactory.doCheck(loginUser);

        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = interfaceInfoInvokeRequest.getId();

        log.info("接口id ===> {}", interfaceInfoInvokeRequest);

        // 包含多个键值对
        String userRequestParams = interfaceInfoInvokeRequest.getUserRequestParams();

        // 入参校验
//        if (StringUtils.isEmpty(userRequestParams)) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口调用异常，请确认参数后再试！");
//        }
//        try {
//            new Gson().toJson(userRequestParams);
//        } catch (Exception e) {
//            log.error("入参校验异常", e);
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口调用异常，请确认参数后再试！");
//        }

        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (oldInterfaceInfo.getStatus() == InterfaceInfoStatusEnum.OFFLINE.getValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口已关闭");
        }

        // ->客户端 ->网关
        Callable<Object> callable = () -> invoke(loginUser, oldInterfaceInfo, userRequestParams);

        // 定义重试器 如果结果为空/IO异常/运行时异常则重试 允许执行3次
        Retryer<Object> retryer = RetryerBuilder.newBuilder()
                .retryIfResult(Objects::isNull)
                .retryIfExceptionOfType(IOException.class)
                .retryIfRuntimeException()
                .withWaitStrategy(WaitStrategies.incrementingWait(1, TimeUnit.SECONDS, 1, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .build();

        Object res;
        try {
            res = retryer.call(callable);
        } catch (RetryException | ExecutionException e) {
            // 重试次数超过阈值或被强制中断
            log.error("invoke ", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用异常，请确认参数后再试！");
        }

        if (res == null || res.toString().contains("Error ")) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用异常，请确认参数后再试！");
        }
        return ResultUtils.success(res);
    }

    private Object invoke(User user, InterfaceInfo interfaceInfo, String userRequestParams) {
        try {
            Class<?> clientClazz = Class.forName(SDK_PACKAGE + interfaceInfo.getSdk());
            Constructor<?> binApiClientConstructor = clientClazz.getConstructor(String.class, String.class);
            Object apiClient = binApiClientConstructor.newInstance(user.getAccessKey(), user.getSecretKey());

            Method[] methods = clientClazz.getMethods();

            for (Method method : methods) {
                if (method.getName().equals(interfaceInfo.getMethod())) {
                    // 无参方法
                    if (StringUtils.isEmpty(userRequestParams) && method.getParameterCount() == 0) {
                        return method.invoke(apiClient);
                    }
                    // 序列化
                    // Object parameter = new Gson().fromJson(userRequestParams, method.getParameterTypes()[0]);
                    // 单参方法
                    return method.invoke(apiClient, userRequestParams);
                }
            }
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "该接口不存在");
        } catch (Exception e) {
            log.error("invoke interfaceInfo:{}", interfaceInfo, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口调用异常，请确认参数后再试！");
        }
    }

    @GetMapping("/interfaceNameList")
    public BaseResponse<Map> interfaceNameList() {
//        List<InterfaceInfo> list = interfaceInfoService.list();
//        Map<String, String> interfaceNameMap = new HashMap<>(list.size());
//        list.forEach(interfaceInfo -> interfaceNameMap.put(interfaceInfo.getName(), interfaceInfo.getName()));
        Map<String, String> interfaceNameMap = Maps.newHashMap();
        BASE_INFO_CACHE.forEach((k, v) -> interfaceNameMap.put(getInterfaceInfoCacheById(k).getName(), getInterfaceInfoCacheById(k).getName()));
        return ResultUtils.success(interfaceNameMap);
    }
}

package org.huanzhang.project.system.service.impl;

import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.huanzhang.common.core.text.Convert;
import org.huanzhang.common.enums.HttpMethod;
import org.huanzhang.common.filter.PropertyPreExcludeFilter;
import org.huanzhang.common.utils.ExceptionUtil;
import org.huanzhang.common.utils.StringUtils;
import org.huanzhang.common.utils.ip.IpUtils;
import org.huanzhang.framework.aspectj.lang.annotation.Log;
import org.huanzhang.framework.aspectj.lang.enums.BusinessStatus;
import org.huanzhang.framework.security.LoginUser;
import org.huanzhang.project.system.converter.SysOperateLogMapper;
import org.huanzhang.project.system.entity.SysOperateLog;
import org.huanzhang.project.system.query.SysOperateLogQuery;
import org.huanzhang.project.system.repository.SysOperateLogRepository;
import org.huanzhang.project.system.service.SysOperateLogService;
import org.huanzhang.project.system.vo.SysOperateLogVO;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 操作日志表 业务处理
 *
 * @author haihuoshu
 * @version 2025-12-19
 */
@Service
@RequiredArgsConstructor
public class SysOperateLogServiceImpl implements SysOperateLogService {

    /**
     * 排除敏感属性字段
     */
    public static final String[] EXCLUDE_PROPERTIES = {"password", "oldPassword", "newPassword", "confirmPassword"};

    private final SysOperateLogRepository sysOperateLogRepository;
    private final SysOperateLogMapper sysOperateLogMapper;

    /**
     * 根据条件查询操日志总数
     */
    @Override
    public Mono<Long> selectOperateLogCount(SysOperateLogQuery query) {
        return sysOperateLogRepository.selectCountByQuery(query);
    }

    /**
     * 根据条件查询操作日志
     */
    @Override
    public Flux<SysOperateLogVO> selectOperateLogList(SysOperateLogQuery query) {
        return sysOperateLogRepository.selectListByQuery(query)
                .map(sysOperateLogMapper::toVo);
    }

    /**
     * 批量删除操作日志
     */
    @Override
    public Mono<Void> deleteOperateLogByIds(List<Long> operIds) {
        return sysOperateLogRepository.deleteByIds(operIds)
                .then();
    }

    /**
     * 清空操作日志
     */
    @Override
    public Mono<Void> cleanOperLog() {
        return sysOperateLogRepository.deleteAll()
                .then();
    }

    /**
     * 新增操作日志
     */
    @Override
    public Mono<Long> insertOperateLog(ServerHttpRequest request, LoginUser loginUser, JoinPoint joinPoint, Log controllerLog, Throwable e, Object jsonResult, long start) {

        // *========数据库日志=========*//
        SysOperateLog operLog = new SysOperateLog();
        operLog.setStatus(BusinessStatus.SUCCESS.ordinal());
        // 请求的地址
        String ip = IpUtils.getIpAddr(request);
        operLog.setOperIp(ip);
        operLog.setOperUrl(StringUtils.substring(request.getURI().getPath(), 0, 255));
        operLog.setOperName(loginUser.getUsername());
//        SysUser currentUser = loginUser.getUser();
//        if (StringUtils.isNotNull(currentUser) && StringUtils.isNotNull(currentUser.getDept())) {
//            operLog.setDeptName(currentUser.getDept().getDeptName());
//        }

        if (e != null) {
            operLog.setStatus(BusinessStatus.FAIL.ordinal());
            operLog.setErrorMsg(StringUtils.substring(Convert.toStr(e.getMessage(), ExceptionUtil.getExceptionMessage(e)), 0, 2000));
        }
        // 设置方法名称
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        operLog.setMethod(className + "." + methodName + "()");
        // 设置请求方式
        operLog.setRequestMethod(request.getMethod().name());
        // 处理设置注解上的参数
        getControllerMethodDescription(request, joinPoint, controllerLog, operLog, jsonResult);
        // 设置消耗时间
        operLog.setCostTime(System.currentTimeMillis() - start);
        // 保存数据库
        return sysOperateLogRepository.insert(operLog);
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     */
    public void getControllerMethodDescription(ServerHttpRequest request, JoinPoint joinPoint, Log log, SysOperateLog operLog, Object jsonResult) {
        // 设置action动作
        operLog.setBusinessType(log.businessType().ordinal());
        // 设置标题
        operLog.setTitle(log.title());
        // 设置操作人类别
        operLog.setOperatorType(log.operatorType().ordinal());
        // 是否需要保存request，参数和值
        if (log.isSaveRequestData()) {
            // 获取参数的信息，传入到数据库中。
            setRequestValue(request, joinPoint, operLog, log.excludeParamNames());
        }
        // 是否需要保存response，参数和值
        if (log.isSaveResponseData() && StringUtils.isNotNull(jsonResult)) {
            operLog.setJsonResult(StringUtils.substring(JSON.toJSONString(jsonResult), 0, 2000));
        }
    }

    /**
     * 获取请求的参数，放到log中
     */
    private void setRequestValue(ServerHttpRequest request, JoinPoint joinPoint, SysOperateLog operLog, String[] excludeParamNames) {
        String requestMethod = operLog.getRequestMethod();
        Map<?, ?> paramsMap = request.getQueryParams();
        if (StringUtils.isEmpty(paramsMap) && StringUtils.equalsAny(requestMethod, HttpMethod.PUT.name(), HttpMethod.POST.name(), HttpMethod.DELETE.name())) {
            String params = argsArrayToString(joinPoint.getArgs(), excludeParamNames);
            operLog.setOperParam(StringUtils.substring(params, 0, 2000));
        } else {
            operLog.setOperParam(StringUtils.substring(JSON.toJSONString(paramsMap, excludePropertyPreFilter(excludeParamNames)), 0, 2000));
        }
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray, String[] excludeParamNames) {
        StringBuilder params = new StringBuilder();
        if (paramsArray != null) {
            for (Object o : paramsArray) {
                if (StringUtils.isNotNull(o) && !isFilterObject(o)) {
                    try {
                        String jsonObj = JSON.toJSONString(o, excludePropertyPreFilter(excludeParamNames));
                        params.append(jsonObj).append(" ");
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        return params.toString().trim();
    }

    /**
     * 忽略敏感属性
     */
    public PropertyPreExcludeFilter excludePropertyPreFilter(String[] excludeParamNames) {
        return new PropertyPreExcludeFilter().addExcludes(ArrayUtils.addAll(EXCLUDE_PROPERTIES, excludeParamNames));
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Object value : map.entrySet()) {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof ServerHttpRequest || o instanceof ServerHttpResponse
                || o instanceof BindingResult;
    }
}

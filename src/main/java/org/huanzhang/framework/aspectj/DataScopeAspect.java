package org.huanzhang.framework.aspectj;

import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.huanzhang.common.constant.UserConstants;
import org.huanzhang.common.core.text.Convert;
import org.huanzhang.common.utils.StringUtils;
import org.huanzhang.framework.aspectj.lang.annotation.DataScope;
import org.huanzhang.framework.security.ReactiveSecurityUtils;
import org.huanzhang.framework.web.domain.AbstractDataScope;
import org.huanzhang.project.system.domain.SysRole;
import org.huanzhang.project.system.domain.SysUser;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据过滤处理
 *
 * @author ruoyi
 */
@Aspect
@Component
public class DataScopeAspect {
    /**
     * 全部数据权限
     */
    public static final String DATA_SCOPE_ALL = "1";

    /**
     * 自定数据权限
     */
    public static final String DATA_SCOPE_CUSTOM = "2";

    /**
     * 部门数据权限
     */
    public static final String DATA_SCOPE_DEPT = "3";

    /**
     * 部门及以下数据权限
     */
    public static final String DATA_SCOPE_DEPT_AND_CHILD = "4";

    /**
     * 仅本人数据权限
     */
    public static final String DATA_SCOPE_SELF = "5";

    @Around("@annotation(org.huanzhang.framework.aspectj.lang.annotation.DataScope)")
    public Publisher<Object> doAround(ProceedingJoinPoint joinPoint) {
        // 获取方法注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        DataScope controllerDataScope = signature.getMethod().getAnnotation(DataScope.class);

        // 获取当前的用户
        return ReactiveSecurityUtils.getLoginUser()
                .flatMapMany(loginUser -> {
                    if (StringUtils.isNotNull(loginUser)) {
                        SysUser currentUser = loginUser.getUser();
                        // 如果是超级管理员，则不过滤数据
                        if (StringUtils.isNotNull(currentUser) && !currentUser.isAdmin()) {
                            return dataScopeFilter(joinPoint, currentUser, controllerDataScope.deptAlias(), controllerDataScope.userAlias());
                        }
                    }
                    return proceedJoinPoint(joinPoint);
                });
    }

    /**
     * 数据范围过滤
     *
     * @param joinPoint 切点
     * @param user      用户
     * @param deptAlias 部门别名
     * @param userAlias 用户别名
     */
    @SneakyThrows
    public static Publisher<Object> dataScopeFilter(ProceedingJoinPoint joinPoint, SysUser user, String deptAlias, String userAlias) {
        StringBuilder sqlString = new StringBuilder();
        List<String> conditions = new ArrayList<>();
        List<String> scopeCustomIds = new ArrayList<>();
        user.getRoles().forEach(role -> {
            if (DATA_SCOPE_CUSTOM.equals(role.getDataScope()) && StringUtils.equals(role.getStatus(), UserConstants.ROLE_NORMAL)) {
                scopeCustomIds.add(Convert.toStr(role.getRoleId()));
            }
        });

        for (SysRole role : user.getRoles()) {
            String dataScope = role.getDataScope();
            if (conditions.contains(dataScope) || StringUtils.equals(role.getStatus(), UserConstants.ROLE_DISABLE)) {
                continue;
            }
            if (DATA_SCOPE_ALL.equals(dataScope)) {
                sqlString = new StringBuilder();
                conditions.add(dataScope);
                break;
            } else if (DATA_SCOPE_CUSTOM.equals(dataScope)) {
                if (scopeCustomIds.size() > 1) {
                    // 多个自定数据权限使用in查询，避免多次拼接。
                    sqlString.append(StringUtils.format(" OR {}.dept_id IN ( SELECT dept_id FROM sys_role_dept WHERE role_id in ({}) ) ", deptAlias, String.join(",", scopeCustomIds)));
                } else {
                    sqlString.append(StringUtils.format(" OR {}.dept_id IN ( SELECT dept_id FROM sys_role_dept WHERE role_id = {} ) ", deptAlias, role.getRoleId()));
                }
            } else if (DATA_SCOPE_DEPT.equals(dataScope)) {
                sqlString.append(StringUtils.format(" OR {}.dept_id = {} ", deptAlias, user.getDeptId()));
            } else if (DATA_SCOPE_DEPT_AND_CHILD.equals(dataScope)) {
                sqlString.append(StringUtils.format(" OR {}.dept_id IN ( SELECT dept_id FROM sys_dept WHERE dept_id = {} or find_in_set( {} , ancestors ) )", deptAlias, user.getDeptId(), user.getDeptId()));
            } else if (DATA_SCOPE_SELF.equals(dataScope)) {
                if (StringUtils.isNotBlank(userAlias)) {
                    sqlString.append(StringUtils.format(" OR {}.user_id = {} ", userAlias, user.getUserId()));
                } else {
                    // 数据权限为仅本人且没有userAlias别名不查询任何数据
                    sqlString.append(StringUtils.format(" OR {}.dept_id = 0 ", deptAlias));
                }
            }
            conditions.add(dataScope);
        }

        // 角色都不包含传递过来的权限字符，这个时候sqlString也会为空，所以要限制一下,不查询任何数据
        if (StringUtils.isEmpty(conditions)) {
            sqlString.append(StringUtils.format(" OR {}.dept_id = 0 ", deptAlias));
        }

        if (StringUtils.isNotBlank(sqlString.toString())) {
            Object params = joinPoint.getArgs()[0];
            if (StringUtils.isNotNull(params) && params instanceof AbstractDataScope ds) {
                ds.setDataScope(" AND (" + sqlString.substring(4) + ")");
            }
        }

        return proceedJoinPoint(joinPoint);
    }

    /**
     * 执行ProceedingJoinPoint，适配WebFlux的响应式返回值（返回Publisher）
     */
    private static Publisher<Object> proceedJoinPoint(ProceedingJoinPoint joinPoint) {
        try {
            Object result = joinPoint.proceed();
            // 将proceed的结果转换为Publisher（处理Mono/Flux/普通对象）
            return convertToPublisher(result);
        } catch (Throwable e) {
            // 处理原方法执行异常：Mono.error和Flux.error都实现了Publisher
            return Mono.error(e);
        }
    }

    /**
     * 将对象转换为Publisher，保留Flux流式特性，适配Mono/Flux/普通对象
     */
    @SuppressWarnings("unchecked")
    private static Publisher<Object> convertToPublisher(Object obj) {
        if (obj == null) {
            // 空值返回空的Mono（也可以返回Flux.empty()，根据业务需求调整）
            return Mono.empty();
        } else if (obj instanceof Publisher) {
            // 如果是Mono/Flux，直接强转返回（保留原有的流式特性）
            return (Publisher<Object>) obj;
        } else {
            // 普通对象包裹为Mono（WebFlux控制器中普通对象会被自动包装，但这里显式处理更规范）
            return Mono.just(obj);
        }
    }
}

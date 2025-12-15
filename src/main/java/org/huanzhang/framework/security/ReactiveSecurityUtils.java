package org.huanzhang.framework.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

/**
 * 安全服务工具类
 *
 * @author ruoyi
 */
public class ReactiveSecurityUtils {

    /**
     * 获取身份验证信息
     */
    private static Mono<Authentication> getAuthentication() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication);
    }

    /**
     * 获取用户
     **/
    public static Mono<LoginUser> getLoginUser() {
        return getAuthentication()
                .map(Authentication::getPrincipal)
                .cast(LoginUser.class);
    }

    public static boolean isAdmin(Long userId) {
        return userId != null && 1L == userId;
    }

    public static boolean isNotAdmin(Long userId) {
        return !isAdmin(userId);
    }

    /**
     * 获取用户ID
     **/
    public static Mono<Long> getUserId() {
        return getLoginUser()
                .map(LoginUser::getUserId);
    }

    /**
     * 获取用户账号
     **/
    public static Mono<String> getUsername() {
        return getLoginUser()
                .map(LoginUser::getUsername);
    }
}

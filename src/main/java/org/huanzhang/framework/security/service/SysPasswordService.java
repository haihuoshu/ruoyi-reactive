package org.huanzhang.framework.security.service;

import org.huanzhang.common.constant.CacheConstants;
import org.huanzhang.common.exception.user.UserPasswordNotMatchException;
import org.huanzhang.common.exception.user.UserPasswordRetryLimitExceedException;
import org.huanzhang.common.utils.SecurityUtils;
import org.huanzhang.framework.redis.ReactiveRedisUtils;
import org.huanzhang.framework.security.context.AuthenticationContextHolder;
import org.huanzhang.project.system.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * 登录密码方法
 *
 * @author ruoyi
 */
@Component
public class SysPasswordService {
    @Autowired
    private ReactiveRedisUtils<Integer> reactiveRedisUtils;

    @Value(value = "${user.password.maxRetryCount}")
    private int maxRetryCount;

    @Value(value = "${user.password.lockTime}")
    private int lockTime;

    /**
     * 登录账户密码错误次数缓存键名
     *
     * @param username 用户名
     * @return 缓存键key
     */
    private String getCacheKey(String username) {
        return CacheConstants.PWD_ERR_CNT_KEY + username;
    }

    public void validate(SysUser user) {
        Authentication usernamePasswordAuthenticationToken = AuthenticationContextHolder.getContext();
        String username = usernamePasswordAuthenticationToken.getName();
        String password = usernamePasswordAuthenticationToken.getCredentials().toString();

        reactiveRedisUtils.getCacheObject(getCacheKey(username))
                .defaultIfEmpty(0)
                .flatMap(retryCount -> {

                    if (retryCount >= maxRetryCount) {
                        return Mono.error(new UserPasswordRetryLimitExceedException(maxRetryCount, lockTime));
                    }

                    if (!matches(user, password)) {
                        retryCount = retryCount + 1;
                        return reactiveRedisUtils.setCacheObject(getCacheKey(username), retryCount, Duration.ofMinutes(lockTime))
                                .then(Mono.error(new UserPasswordNotMatchException()));
                    } else {
                        return clearLoginRecordCache(username);
                    }
                })
                .subscribe();
    }

    public boolean matches(SysUser user, String rawPassword) {
        return SecurityUtils.matchesPassword(rawPassword, user.getPassword());
    }

    public Mono<Long> clearLoginRecordCache(String loginName) {
        return reactiveRedisUtils.deleteCache(getCacheKey(loginName));
    }
}

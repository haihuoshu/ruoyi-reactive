package org.huanzhang.framework.security.service;

import lombok.RequiredArgsConstructor;
import org.huanzhang.common.constant.CacheConstants;
import org.huanzhang.common.constant.Constants;
import org.huanzhang.common.constant.UserConstants;
import org.huanzhang.common.exception.ServiceException;
import org.huanzhang.common.exception.user.*;
import org.huanzhang.common.utils.MessageUtils;
import org.huanzhang.common.utils.StringUtils;
import org.huanzhang.common.utils.ip.IpUtils;
import org.huanzhang.framework.redis.ReactiveRedisUtils;
import org.huanzhang.framework.security.LoginUser;
import org.huanzhang.framework.security.SysAccessLogApi;
import org.huanzhang.framework.security.context.AuthenticationContextHolder;
import org.huanzhang.project.system.dto.SysUserInsertDTO;
import org.huanzhang.project.system.service.SysConfigService;
import org.huanzhang.project.system.service.SysUserService;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 登录校验方法
 *
 * @author ruoyi
 */
@Component
@RequiredArgsConstructor
public class SysLoginService {

    private final SysUserService sysUserService;

    private final SysConfigService sysConfigService;

    private final SysAccessLogApi sysAccessLogApi;

    private final TokenService tokenService;

    private final ReactiveAuthenticationManager reactiveAuthenticationManager;

    private final ReactiveRedisUtils<String> reactiveRedisUtils;

    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @param code     验证码
     * @param uuid     唯一标识
     * @return 结果
     */
    public Mono<String> login(ServerHttpRequest request, String username, String password, String code, String uuid) {
        // 验证码校验
        validateCaptcha(request, username, code, uuid);
        // 登录前置校验
        loginPreCheck(request, username, password);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        AuthenticationContextHolder.setContext(authenticationToken);
        // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
        return reactiveAuthenticationManager.authenticate(authenticationToken)
                .onErrorResume(e -> {
                    if (e instanceof BadCredentialsException) {
                        return sysAccessLogApi.insertAccessLog(request, username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match"))
                                .then(Mono.error(new UserPasswordNotMatchException()));
                    } else {
                        return sysAccessLogApi.insertAccessLog(request, username, Constants.LOGIN_FAIL, e.getMessage())
                                .then(Mono.error(new ServiceException(e.getMessage())));
                    }
                })
                .flatMap(authentication -> {
                    // 记录日志
                    return sysAccessLogApi.insertAccessLog(request, username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"))
                            .then(Mono.fromCallable(() -> {
                                LoginUser loginUser = (LoginUser) authentication.getPrincipal();
                                recordLoginInfo(request, loginUser.getUserId());
                                // 生成token
                                return tokenService.createToken(request, loginUser);
                            }));
                });
    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code     验证码
     * @param uuid     唯一标识
     */
    public void validateCaptcha(ServerHttpRequest request, String username, String code, String uuid) {
        sysConfigService.selectCaptchaEnabled()
                .flatMap(captchaEnabled -> {
                    if (captchaEnabled) {
                        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
                        return reactiveRedisUtils.getCacheObject(verifyKey)
                                .switchIfEmpty(sysAccessLogApi.insertAccessLog(request, username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire"))
                                        .then(Mono.error(new CaptchaExpireException()))
                                )
                                .flatMap(captcha -> {
                                    // 删除缓存
                                    return reactiveRedisUtils.deleteCache(verifyKey)
                                            .then(Mono.defer(() -> {
                                                if (!code.equalsIgnoreCase(captcha)) {
                                                    return sysAccessLogApi.insertAccessLog(request, username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error"))
                                                            .then(Mono.error(new CaptchaException()));
                                                }
                                                return Mono.empty();
                                            }));
                                });

                    }
                    return Mono.empty();
                })
                .subscribe();
    }

    /**
     * 登录前置校验
     *
     * @param username 用户名
     * @param password 用户密码
     */
    public void loginPreCheck(ServerHttpRequest request, String username, String password) {
        // 用户名或密码为空 错误
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            sysAccessLogApi.insertAccessLog(request, username, Constants.LOGIN_FAIL, MessageUtils.message("not.null")).subscribe();
            throw new UserNotExistsException();
        }
        // 密码如果不在指定范围内 错误
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            sysAccessLogApi.insertAccessLog(request, username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")).subscribe();
            throw new UserPasswordNotMatchException();
        }
        // 用户名不在指定范围内 错误
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            sysAccessLogApi.insertAccessLog(request, username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")).subscribe();
            throw new UserPasswordNotMatchException();
        }
        // IP黑名单校验
        sysConfigService.selectConfigByKey("sys.login.blackIPList")
                .flatMap(blackStr -> {
                    if (IpUtils.isMatchedIp(blackStr, IpUtils.getIpAddr(request))) {
                        return sysAccessLogApi.insertAccessLog(request, username, Constants.LOGIN_FAIL, MessageUtils.message("login.blocked"))
                                .then(Mono.error(new BlackListException()));
                    }
                    return Mono.empty();
                })
                .subscribe();
    }

    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    public void recordLoginInfo(@SuppressWarnings("unused") ServerHttpRequest request, Long userId) {
        SysUserInsertDTO sysUserInsertDTO = new SysUserInsertDTO();
        sysUserInsertDTO.setUserId(userId);
//        sysUserInsertDTO.setLoginIp(IpUtils.getIpAddr(request));
//        sysUserInsertDTO.setLoginDate(DateUtils.getNowDate());
        sysUserService.updateUserProfile(sysUserInsertDTO);
    }
}

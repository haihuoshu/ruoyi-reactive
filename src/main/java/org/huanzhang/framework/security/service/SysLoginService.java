package org.huanzhang.framework.security.service;

import jakarta.annotation.Resource;
import org.huanzhang.common.constant.CacheConstants;
import org.huanzhang.common.constant.Constants;
import org.huanzhang.common.constant.UserConstants;
import org.huanzhang.common.exception.ServiceException;
import org.huanzhang.common.exception.user.*;
import org.huanzhang.common.utils.DateUtils;
import org.huanzhang.common.utils.MessageUtils;
import org.huanzhang.common.utils.StringUtils;
import org.huanzhang.common.utils.ip.IpUtils;
import org.huanzhang.framework.manager.factory.AsyncFactory;
import org.huanzhang.framework.redis.RedisCache;
import org.huanzhang.framework.security.LoginUser;
import org.huanzhang.framework.security.context.AuthenticationContextHolder;
import org.huanzhang.project.system.domain.SysUser;
import org.huanzhang.project.system.service.ISysConfigService;
import org.huanzhang.project.system.service.ISysUserService;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * 登录校验方法
 *
 * @author ruoyi
 */
@Component
public class SysLoginService {
    @Resource
    private TokenService tokenService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private RedisCache redisCache;

    @Resource
    private ISysUserService userService;

    @Resource
    private ISysConfigService configService;

    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @param code     验证码
     * @param uuid     唯一标识
     * @return 结果
     */
    public String login(ServerHttpRequest request, String username, String password, String code, String uuid) {
        // 验证码校验
        validateCaptcha(request, username, code, uuid);
        // 登录前置校验
        loginPreCheck(request, username, password);
        // 用户验证
        Authentication authentication;
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            AuthenticationContextHolder.setContext(authenticationToken);
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                AsyncFactory.recordLogininfor(request, username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match"));
                throw new UserPasswordNotMatchException();
            } else {
                AsyncFactory.recordLogininfor(request, username, Constants.LOGIN_FAIL, e.getMessage());
                throw new ServiceException(e.getMessage());
            }
        } finally {
            AuthenticationContextHolder.clearContext();
        }
        AsyncFactory.recordLogininfor(request, username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"));

        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        recordLoginInfo(request, loginUser.getUserId());
        // 生成token
        return tokenService.createToken(request, loginUser);
    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code     验证码
     * @param uuid     唯一标识
     */
    public void validateCaptcha(ServerHttpRequest request, String username, String code, String uuid) {
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled) {
            String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
            String captcha = redisCache.getCacheObject(verifyKey);
            if (captcha == null) {
                AsyncFactory.recordLogininfor(request, username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire"));
                throw new CaptchaExpireException();
            }
            redisCache.deleteObject(verifyKey);
            if (!code.equalsIgnoreCase(captcha)) {
                AsyncFactory.recordLogininfor(request, username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error"));
                throw new CaptchaException();
            }
        }
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
            AsyncFactory.recordLogininfor(request, username, Constants.LOGIN_FAIL, MessageUtils.message("not.null"));
            throw new UserNotExistsException();
        }
        // 密码如果不在指定范围内 错误
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            AsyncFactory.recordLogininfor(request, username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match"));
            throw new UserPasswordNotMatchException();
        }
        // 用户名不在指定范围内 错误
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            AsyncFactory.recordLogininfor(request, username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match"));
            throw new UserPasswordNotMatchException();
        }
        // IP黑名单校验
        String blackStr = configService.selectConfigByKey("sys.login.blackIPList");
        if (IpUtils.isMatchedIp(blackStr, IpUtils.getIpAddr(request))) {
            AsyncFactory.recordLogininfor(request, username, Constants.LOGIN_FAIL, MessageUtils.message("login.blocked"));
            throw new BlackListException();
        }
    }

    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    public void recordLoginInfo(ServerHttpRequest request, Long userId) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setLoginIp(IpUtils.getIpAddr(request));
        sysUser.setLoginDate(DateUtils.getNowDate());
        userService.updateUserProfile(sysUser);
    }
}

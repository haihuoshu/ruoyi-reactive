package org.huanzhang.framework.security.service;

import jakarta.annotation.Resource;
import org.huanzhang.common.constant.CacheConstants;
import org.huanzhang.common.constant.Constants;
import org.huanzhang.common.constant.UserConstants;
import org.huanzhang.common.exception.user.CaptchaException;
import org.huanzhang.common.exception.user.CaptchaExpireException;
import org.huanzhang.common.utils.MessageUtils;
import org.huanzhang.common.utils.SecurityUtils;
import org.huanzhang.common.utils.StringUtils;
import org.huanzhang.framework.manager.AsyncManager;
import org.huanzhang.framework.manager.factory.AsyncFactory;
import org.huanzhang.framework.redis.ReactiveRedisUtils;
import org.huanzhang.framework.security.RegisterBody;
import org.huanzhang.project.system.dto.SysUserInsertDTO;
import org.huanzhang.project.system.service.SysConfigService;
import org.huanzhang.project.system.service.SysUserService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 注册校验方法
 *
 * @author ruoyi
 */
@Component
public class SysRegisterService {
    @Resource
    private SysUserService userService;

    @Resource
    private SysConfigService configService;

    @Resource
    private ReactiveRedisUtils<String> reactiveRedisUtils;

    /**
     * 注册
     */
    public Mono<String> register(RegisterBody registerBody) {
        String username = registerBody.getUsername(), password = registerBody.getPassword();
        SysUserInsertDTO sysUserInsertDTO = new SysUserInsertDTO();
        sysUserInsertDTO.setUserName(username);

        // 验证码开关
        return configService.selectCaptchaEnabled()
                .flatMap(captchaEnabled -> {
                    if (captchaEnabled) {
                        validateCaptcha(registerBody.getCode(), registerBody.getUuid());
                    }

                    String msg;
                    if (StringUtils.isEmpty(username)) {
                        msg = "用户名不能为空";
                    } else if (StringUtils.isEmpty(password)) {
                        msg = "用户密码不能为空";
                    } else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                            || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
                        msg = "账户长度必须在2到20个字符之间";
                    } else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                            || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
                        msg = "密码长度必须在5到20个字符之间";
                    } else {
                        sysUserInsertDTO.setNickName(username);
                        sysUserInsertDTO.setPassword(SecurityUtils.encryptPassword(password));
                        return userService.insertUser(sysUserInsertDTO)
                                .then(Mono.defer(() -> {
                                    AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.REGISTER, MessageUtils.message("user.register.success")));
                                    return Mono.empty();
                                }))
                                .thenReturn("");
                    }
                    return Mono.just(msg);
                });
    }

    /**
     * 校验验证码
     *
     * @param code 验证码
     * @param uuid 唯一标识
     */
    public void validateCaptcha(String code, String uuid) {
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
        reactiveRedisUtils.getCacheObject(verifyKey)
                .switchIfEmpty(Mono.error(new CaptchaExpireException()))
                .flatMap(captcha -> {
                    // 删除缓存
                    return reactiveRedisUtils.deleteCache(verifyKey)
                            .then(Mono.defer(() -> {
                                if (!code.equalsIgnoreCase(captcha)) {
                                    return Mono.error(new CaptchaException());
                                }
                                return Mono.empty();
                            }));

                })
                .subscribe();
    }
}

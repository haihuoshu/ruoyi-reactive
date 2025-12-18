package org.huanzhang.framework.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.huanzhang.common.enums.UserStatus;
import org.huanzhang.common.exception.ServiceException;
import org.huanzhang.common.utils.MessageUtils;
import org.huanzhang.framework.security.LoginUser;
import org.huanzhang.project.system.entity.SysUser;
import org.huanzhang.project.system.repository.SysUserRepository;
import org.huanzhang.project.system.service.SysMenuService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 用户验证处理
 *
 * @author ruoyi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final SysUserRepository sysUserRepository;

//    private final SysPasswordService passwordService;

    private final SysMenuService sysMenuService;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return sysUserRepository.selectOneByUserName(username)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("登录用户：{} 不存在.", username);
                    throw new ServiceException(MessageUtils.message("user.not.exists"));
                }))
                .flatMap(user -> {
                    if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
                        log.info("登录用户：{} 已被停用.", username);
                        return ServiceException.monoInstance(MessageUtils.message("user.blocked"));
                    }

//                    passwordService.validate(user);

                    return createLoginUser(user);
                });
    }

    public Mono<UserDetails> createLoginUser(SysUser user) {
        return sysMenuService.selectMenuPermsByUserId(user.getUserId())
                .map(permissions -> {
                    // 登录用户
                    return new LoginUser(user.getUserId(), user.getDeptId(), user, permissions);
                });
    }

}

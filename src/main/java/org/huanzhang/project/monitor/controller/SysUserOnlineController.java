package org.huanzhang.project.monitor.controller;

import org.huanzhang.common.constant.CacheConstants;
import org.huanzhang.common.utils.StringUtils;
import org.huanzhang.framework.aspectj.lang.annotation.Log;
import org.huanzhang.framework.aspectj.lang.enums.BusinessType;
import org.huanzhang.framework.redis.ReactiveRedisUtils;
import org.huanzhang.framework.security.LoginUser;
import org.huanzhang.framework.web.controller.BaseController;
import org.huanzhang.framework.web.domain.AjaxResponse;
import org.huanzhang.framework.web.domain.PageResponse;
import org.huanzhang.project.monitor.domain.SysUserOnline;
import org.huanzhang.project.system.service.ISysUserOnlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Collections;

/**
 * 在线用户监控
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/online")
public class SysUserOnlineController extends BaseController {
    @Autowired
    private ISysUserOnlineService userOnlineService;

    @Autowired
    private ReactiveRedisUtils<LoginUser> reactiveRedisUtils;

    @PreAuthorize("hasAuthority('monitor:online:list')")
    @GetMapping("/list")
    public Mono<PageResponse<SysUserOnline>> list(String ipaddr, String userName) {
        return reactiveRedisUtils.cacheKeys(CacheConstants.LOGIN_TOKEN_KEY + "*")
                .flatMap(key -> {
                    // 获取
                    return reactiveRedisUtils.getCacheObject(key)
                            .map(user -> {
                                if (StringUtils.isNotEmpty(ipaddr) && StringUtils.isNotEmpty(userName)) {
                                    return userOnlineService.selectOnlineByInfo(ipaddr, userName, user);
                                } else if (StringUtils.isNotEmpty(ipaddr)) {
                                    return userOnlineService.selectOnlineByIpaddr(ipaddr, user);
                                } else if (StringUtils.isNotEmpty(userName) && StringUtils.isNotNull(user.getUser())) {
                                    return userOnlineService.selectOnlineByUserName(userName, user);
                                } else {
                                    return userOnlineService.loginUserToUserOnline(user);
                                }
                            });
                })
                .collectList()
                .map(userOnlineList -> {
                    Collections.reverse(userOnlineList);
                    userOnlineList.removeAll(Collections.singleton(null));
                    return PageResponse.getInstance(userOnlineList, userOnlineList.size());
                });
    }

    /**
     * 强退用户
     */
    @PreAuthorize("hasAuthority('monitor:online:forceLogout')")
    @Log(title = "在线用户", businessType = BusinessType.FORCE)
    @DeleteMapping("/{tokenId}")
    public Mono<AjaxResponse<Void>> forceLogout(@PathVariable String tokenId) {
        return reactiveRedisUtils.deleteCache(CacheConstants.LOGIN_TOKEN_KEY + tokenId)
                .thenReturn(AjaxResponse.ok());
    }
}

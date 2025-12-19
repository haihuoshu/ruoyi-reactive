package org.huanzhang.project.system.service.impl;

import eu.bitwalker.useragentutils.UserAgent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.huanzhang.common.constant.Constants;
import org.huanzhang.common.utils.LogUtils;
import org.huanzhang.common.utils.StringUtils;
import org.huanzhang.common.utils.ip.AddressUtils;
import org.huanzhang.common.utils.ip.IpUtils;
import org.huanzhang.project.system.converter.SysAccessLogMapper;
import org.huanzhang.project.system.entity.SysAccessLog;
import org.huanzhang.project.system.query.SysAccessLogQuery;
import org.huanzhang.project.system.repository.SysAccessLogRepository;
import org.huanzhang.project.system.service.SysAccessLogService;
import org.huanzhang.project.system.vo.SysAccessLogVO;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 访问日志表 业务处理
 *
 * @author haihuoshu
 * @version 2025-12-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysAccessLogServiceImpl implements SysAccessLogService {

    private final SysAccessLogRepository sysAccessLogRepository;
    private final SysAccessLogMapper sysAccessLogMapper;

    /**
     * 根据条件查询日志总数
     */
    @Override
    public Mono<Long> selectAccessLogCount(SysAccessLogQuery query) {
        return sysAccessLogRepository.selectCountByQuery(query);
    }

    /**
     * 根据条件查询访问日志
     */
    @Override
    public Flux<SysAccessLogVO> selectAccessLogList(SysAccessLogQuery query) {
        return sysAccessLogRepository.selectListByQuery(query)
                .map(sysAccessLogMapper::toVo);
    }

    /**
     * 批量删除访问日志
     */
    @Override
    public Mono<Void> deleteAccessLogByIds(List<Long> infoIds) {
        return sysAccessLogRepository.deleteLogininforByIds(infoIds)
                .then();
    }

    /**
     * 清空访问日志
     */
    @Override
    public Mono<Void> cleanAccessLog() {
        return sysAccessLogRepository.cleanLogininfor()
                .then();
    }

    /**
     * 新增访问日志
     */
    @Override
    public Mono<Long> insertAccessLog(ServerHttpRequest request, String username, String status, String message, Object... args) {
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeaders().getFirst("User-Agent"));
        String ip = IpUtils.getIpAddr(request);

        String address = AddressUtils.getRealAddressByIP(ip);
        String s = LogUtils.getBlock(ip) +
                address +
                LogUtils.getBlock(username) +
                LogUtils.getBlock(status) +
                LogUtils.getBlock(message);
        // 打印信息到日志
        log.info(s, args);
        // 获取客户端操作系统
        String os = userAgent.getOperatingSystem().getName();
        // 获取客户端浏览器
        String browser = userAgent.getBrowser().getName();
        // 封装对象
        SysAccessLog entity = new SysAccessLog();
        entity.setUserName(username);
        entity.setIpaddr(ip);
        entity.setLoginLocation(address);
        entity.setBrowser(browser);
        entity.setOs(os);
        entity.setMsg(message);
        // 日志状态
        if (StringUtils.equalsAny(status, Constants.LOGIN_SUCCESS, Constants.LOGOUT, Constants.REGISTER)) {
            entity.setStatus(Constants.SUCCESS);
        } else if (Constants.LOGIN_FAIL.equals(status)) {
            entity.setStatus(Constants.FAIL);
        }
        // 插入数据
        return sysAccessLogRepository.insert(entity);
    }

}

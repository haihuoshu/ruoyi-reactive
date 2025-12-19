package org.huanzhang.project.system.service;

import org.huanzhang.framework.security.SysAccessLogApi;
import org.huanzhang.project.system.query.SysAccessLogQuery;
import org.huanzhang.project.system.vo.SysAccessLogVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 访问日志表 业务层
 *
 * @author haihuoshu
 * @version 2025-12-15
 */
public interface SysAccessLogService extends SysAccessLogApi {

    /**
     * 根据条件查询日志总数
     */
    Mono<Long> selectAccessLogCount(SysAccessLogQuery query);

    /**
     * 根据条件查询访问日志
     */
    Flux<SysAccessLogVO> selectAccessLogList(SysAccessLogQuery query);

    /**
     * 批量删除访问日志
     */
    Mono<Void> deleteAccessLogByIds(List<Long> infoIds);

    /**
     * 清空访问日志
     */
    Mono<Void> cleanAccessLog();

}

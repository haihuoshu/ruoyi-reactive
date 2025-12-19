package org.huanzhang.project.system.repository;

import org.huanzhang.project.system.entity.SysAccessLog;
import org.huanzhang.project.system.query.SysAccessLogQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 访问日志表 数据层
 *
 * @author haihuoshu
 * @version 2025-12-15
 */
public interface SysAccessLogRepository {

    /**
     * 根据条件查询总数
     */
    Mono<Long> selectCountByQuery(SysAccessLogQuery query);

    /**
     * 根据条件查询列表
     */
    Flux<SysAccessLog> selectListByQuery(SysAccessLogQuery query);

    /**
     * 新增
     */
    Mono<Long> insert(SysAccessLog entity);

    /**
     * 根据日志ID批量删除
     */
    Mono<Long> deleteLogininforByIds(List<Long> infoIds);

    /**
     * 清空
     */
    Mono<Long> cleanLogininfor();

}

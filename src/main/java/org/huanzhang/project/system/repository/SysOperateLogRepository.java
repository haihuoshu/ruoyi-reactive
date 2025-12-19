package org.huanzhang.project.system.repository;

import org.huanzhang.project.system.entity.SysOperateLog;
import org.huanzhang.project.system.query.SysOperateLogQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 操作日志表 数据层
 *
 * @author haihuoshu
 * @version 2025-12-19
 */
public interface SysOperateLogRepository {

    /**
     * 根据条件查询总数
     */
    Mono<Long> selectCountByQuery(SysOperateLogQuery query);

    /**
     * 根据条件查询列表
     */
    Flux<SysOperateLog> selectListByQuery(SysOperateLogQuery query);

    /**
     * 批量删除操作日志
     */
    Mono<Long> deleteByIds(List<Long> operIds);

    /**
     * 清空操作日志
     */
    Mono<Long> deleteAll();

    /**
     * 新增操作日志
     */
    Mono<Long> insert(SysOperateLog entity);

}

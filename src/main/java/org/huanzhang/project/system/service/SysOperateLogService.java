package org.huanzhang.project.system.service;

import org.huanzhang.framework.security.SysOperateLogApi;
import org.huanzhang.project.system.query.SysOperateLogQuery;
import org.huanzhang.project.system.vo.SysOperateLogVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 操作日志表 业务层
 *
 * @author haihuoshu
 * @version 2025-12-19
 */
public interface SysOperateLogService extends SysOperateLogApi {

    /**
     * 根据条件查询操日志总数
     */
    Mono<Long> selectOperateLogCount(SysOperateLogQuery query);

    /**
     * 根据条件查询操作日志
     */
    Flux<SysOperateLogVO> selectOperateLogList(SysOperateLogQuery query);

    /**
     * 批量删除操作日志
     */
    Mono<Void> deleteOperateLogByIds(List<Long> operIds);

    /**
     * 清空操作日志
     */
    Mono<Void> cleanOperLog();

}

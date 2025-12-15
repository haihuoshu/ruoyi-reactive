package org.huanzhang.project.system.repository;

import org.huanzhang.framework.r2dbc.repository.AuditableRepository;
import org.huanzhang.project.system.entity.SysNotice;
import org.huanzhang.project.system.query.SysNoticeQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 通告表 数据层
 *
 * @author haihuoshu
 * @version 2025-12-15
 */
public interface SysNoticeRepository extends AuditableRepository<SysNotice> {

    /**
     * 根据条件查询总数
     */
    Mono<Long> selectCountByQuery(SysNoticeQuery query);

    /**
     * 根据条件查询列表
     */
    Flux<SysNotice> selectListByQuery(SysNoticeQuery query);

    /**
     * 根据主键查询
     */
    Mono<SysNotice> selectOneById(Long noticeId);

    /**
     * 新增
     */
    Mono<Long> insert(SysNotice notice);

    /**
     * 根据主键修改
     */
    Mono<Long> updateById(SysNotice notice);

    /**
     * 根据主键批量删除
     */
    Mono<Long> deleteByIds(List<Long> noticeIds);

}
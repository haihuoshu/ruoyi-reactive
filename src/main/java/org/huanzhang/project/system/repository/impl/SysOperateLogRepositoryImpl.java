package org.huanzhang.project.system.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.r2dbc.R2DBCQuery;
import com.querydsl.r2dbc.R2DBCQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.huanzhang.project.system.entity.SysOperateLog;
import org.huanzhang.project.system.entity.impl.QSysOperateLog;
import org.huanzhang.project.system.query.SysOperateLogQuery;
import org.huanzhang.project.system.repository.SysOperateLogRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

/**
 * 操作日志表 数据处理
 *
 * @author haihuoshu
 * @version 2025-12-19
 */
@Repository
@RequiredArgsConstructor
public class SysOperateLogRepositoryImpl implements SysOperateLogRepository {

    private final QSysOperateLog tb_1_ = new QSysOperateLog("tb_1_");

    private final R2DBCQueryFactory queryFactory;

    /**
     * 根据条件查询总数
     */
    @Override
    public Mono<Long> selectCountByQuery(SysOperateLogQuery query) {
        return getR2dbcQuery(query)
                .fetchCount();
    }

    private R2DBCQuery<SysOperateLog> getR2dbcQuery(SysOperateLogQuery query) {
        BooleanBuilder predicate = new BooleanBuilder();
        // 操作模块
        if (StringUtils.isNotBlank(query.getTitle())) {
            predicate.and(tb_1_.title.contains(query.getTitle()));
        }
        // 业务类型
        if (Objects.nonNull(query.getBusinessType())) {
            predicate.and(tb_1_.businessType.eq(query.getBusinessType()));
        }
        // 操作状态（0正常 1异常）
        if (Objects.nonNull(query.getStatus())) {
            predicate.and(tb_1_.status.eq(query.getStatus()));
        }
        // 操作地址
        if (Objects.nonNull(query.getOperIp())) {
            predicate.and(tb_1_.operIp.contains(query.getOperIp()));
        }
        // 操作时间
        if (ObjectUtils.allNotNull(query.getStartTime())) {
            predicate.and(tb_1_.operTime.goe(LocalDateTime.of(query.getStartTime(), LocalTime.MIN)));
        }
        if (ObjectUtils.allNotNull(query.getEndTime())) {
            predicate.and(tb_1_.operTime.loe(LocalDateTime.of(query.getEndTime(), LocalTime.MAX)));
        }

        return queryFactory.selectDistinct(tb_1_)
                .from(tb_1_)
                .where(predicate)
                .orderBy(tb_1_.operTime.desc());
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Flux<SysOperateLog> selectListByQuery(SysOperateLogQuery query) {
        R2DBCQuery<SysOperateLog> r2dbcQuery = getR2dbcQuery(query);
        if (ObjectUtils.allNotNull(query.getPageNum(), query.getPageSize())) {
            PageRequest pageable = PageRequest.of(query.getPageNum() - 1, query.getPageSize());
            r2dbcQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
        }
        return r2dbcQuery.fetch();
    }

    /**
     * 批量删除操作日志
     */
    @Override
    public Mono<Long> deleteByIds(List<Long> operIds) {
        return queryFactory.delete(tb_1_)
                .where(tb_1_.operId.in(operIds))
                .execute();
    }

    /**
     * 清空操作日志
     */
    @Override
    public Mono<Long> deleteAll() {
        return queryFactory.delete(tb_1_)
                .execute();
    }

    /**
     * 新增操作日志
     */
    @Override
    public Mono<Long> insert(SysOperateLog entity) {
        return queryFactory.insert(tb_1_)
                .populate(entity)
                .execute();
    }
}

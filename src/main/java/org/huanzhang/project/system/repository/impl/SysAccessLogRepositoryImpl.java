package org.huanzhang.project.system.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.r2dbc.R2DBCQuery;
import com.querydsl.r2dbc.R2DBCQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.huanzhang.project.system.entity.SysAccessLog;
import org.huanzhang.project.system.entity.impl.QSysAccessLog;
import org.huanzhang.project.system.query.SysAccessLogQuery;
import org.huanzhang.project.system.repository.SysAccessLogRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

/**
 * 访问日志表 数据处理
 *
 * @author haihuoshu
 * @version 2025-12-15
 */
@Repository
@RequiredArgsConstructor
public class SysAccessLogRepositoryImpl implements SysAccessLogRepository {

    private final QSysAccessLog tb_1_ = new QSysAccessLog("tb_1_");

    private final R2DBCQueryFactory queryFactory;

    /**
     * 根据条件查询总数
     */
    @Override
    public Mono<Long> selectCountByQuery(SysAccessLogQuery query) {
        return getR2dbcQuery(query)
                .fetchCount();
    }

    private R2DBCQuery<SysAccessLog> getR2dbcQuery(SysAccessLogQuery query) {
        BooleanBuilder predicate = new BooleanBuilder();
        // 用户账号
        if (StringUtils.isNotBlank(query.getUserName())) {
            predicate.and(tb_1_.userName.contains(query.getUserName()));
        }
        // 登录状态（0成功 1失败）
        if (StringUtils.isNotBlank(query.getStatus())) {
            predicate.and(tb_1_.status.eq(query.getStatus()));
        }
        // 登录IP地址
        if (Objects.nonNull(query.getIpaddr())) {
            predicate.and(tb_1_.ipaddr.contains(query.getIpaddr()));
        }
        // 访问时间
        if (ObjectUtils.allNotNull(query.getStartTime())) {
            predicate.and(tb_1_.loginTime.goe(LocalDateTime.of(query.getStartTime(), LocalTime.MIN)));
        }
        if (ObjectUtils.allNotNull(query.getEndTime())) {
            predicate.and(tb_1_.loginTime.loe(LocalDateTime.of(query.getEndTime(), LocalTime.MAX)));
        }

        return queryFactory.selectDistinct(tb_1_)
                .from(tb_1_)
                .where(predicate)
                .orderBy(tb_1_.loginTime.desc());
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Flux<SysAccessLog> selectListByQuery(SysAccessLogQuery query) {
        return getR2dbcQuery(query)
                .fetch();
    }

    /**
     * 新增
     */
    @Override
    public Mono<Long> insert(SysAccessLog entity) {
        return queryFactory.insert(tb_1_)
                .populate(entity)
                .execute();
    }

    /**
     * 根据日志ID批量删除
     */
    @Override
    public Mono<Long> deleteLogininforByIds(List<Long> infoIds) {
        return queryFactory.delete(tb_1_)
                .where(tb_1_.infoId.in(infoIds))
                .execute();
    }

    /**
     * 清空
     */
    @Override
    public Mono<Long> cleanLogininfor() {
        return queryFactory.delete(tb_1_)
                .execute();
    }

}

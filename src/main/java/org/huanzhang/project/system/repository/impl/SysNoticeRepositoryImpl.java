package org.huanzhang.project.system.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.r2dbc.R2DBCQuery;
import com.querydsl.r2dbc.R2DBCQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.huanzhang.project.system.entity.SysNotice;
import org.huanzhang.project.system.entity.impl.QSysNotice;
import org.huanzhang.project.system.query.SysNoticeQuery;
import org.huanzhang.project.system.repository.SysNoticeRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 通告表 数据处理
 *
 * @author haihuoshu
 * @version 2025-12-15
 */
@Repository
@RequiredArgsConstructor
public class SysNoticeRepositoryImpl implements SysNoticeRepository {

    private static final QSysNotice tb_1_ = new QSysNotice("tb_1_");

    private final R2DBCQueryFactory queryFactory;

    /**
     * 根据条件查询总数
     */
    @Override
    public Mono<Long> selectCountByQuery(SysNoticeQuery query) {
        return queryFactory.selectFrom(tb_1_)
                .where(getPredicate(query))
                .fetchCount();
    }

    private static BooleanBuilder getPredicate(SysNoticeQuery query) {
        BooleanBuilder predicate = new BooleanBuilder();
        // 通告标题
        if (StringUtils.isNotBlank(query.getNoticeTitle())) {
            predicate.and(tb_1_.noticeTitle.contains(query.getNoticeTitle()));
        }
        // 通告类型（1通知 2公告）
        if (StringUtils.isNotBlank(query.getNoticeType())) {
            predicate.and(tb_1_.noticeType.eq(query.getNoticeType()));
        }
        // 创建人
        if (StringUtils.isNotBlank(query.getCreateBy())) {
            predicate.and(tb_1_.createBy.contains(query.getCreateBy()));
        }
        return predicate;
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Flux<SysNotice> selectListByQuery(SysNoticeQuery query) {
        R2DBCQuery<SysNotice> r2dbcQuery = queryFactory.selectFrom(tb_1_)
                .where(getPredicate(query));

        if (ObjectUtils.allNotNull(query.getPageNum(), query.getPageSize())) {
            PageRequest pageable = PageRequest.of(query.getPageNum() - 1, query.getPageSize());
            r2dbcQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
        }

        return r2dbcQuery.fetch();
    }

    /**
     * 根据主键查询
     */
    @Override
    public Mono<SysNotice> selectOneById(Long noticeId) {
        return queryFactory.selectFrom(tb_1_)
                .where(tb_1_.noticeId.eq(noticeId))
                .fetchOne();
    }

    /**
     * 新增
     */
    @Override
    public Mono<Long> insert(SysNotice notice) {
        return beforeInsert(notice)
                .flatMap(entity -> {
                    // 执行新增
                    return queryFactory.insert(tb_1_)
                            .populate(entity)
                            .execute();
                });
    }

    /**
     * 根据主键修改
     */
    @Override
    public Mono<Long> updateById(SysNotice notice) {
        return beforeUpdate(notice)
                .flatMap(entity -> {
                    // 执行修改
                    return queryFactory.update(tb_1_)
                            .populate(entity)
                            .where(tb_1_.noticeId.eq(entity.getNoticeId()))
                            .execute();
                });
    }

    /**
     * 根据主键批量删除
     */
    @Override
    public Mono<Long> deleteByIds(List<Long> noticeIds) {
        return queryFactory.delete(tb_1_)
                .where(tb_1_.noticeId.in(noticeIds))
                .execute();
    }

}

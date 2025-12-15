package org.huanzhang.project.system.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.r2dbc.R2DBCQuery;
import com.querydsl.r2dbc.R2DBCQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.huanzhang.project.system.entity.SysPost;
import org.huanzhang.project.system.entity.impl.QSysPost;
import org.huanzhang.project.system.query.SysPostQuery;
import org.huanzhang.project.system.repository.SysPostRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 岗位表 数据处理
 *
 * @author haihuoshu
 * @version 2025-12-15
 */
@Repository
@RequiredArgsConstructor
public class SysPostRepositoryImpl implements SysPostRepository {

    private static final QSysPost tb_1_ = new QSysPost("tb_1_");

    private final R2DBCQueryFactory queryFactory;

    /**
     * 根据条件查询总数
     */
    @Override
    public Mono<Long> selectCountByQuery(SysPostQuery query) {
        return queryFactory.selectFrom(tb_1_)
                .where(getPredicate(query))
                .fetchCount();
    }

    private static BooleanBuilder getPredicate(SysPostQuery query) {
        BooleanBuilder predicate = new BooleanBuilder();
        // 岗位名称
        if (StringUtils.isNotBlank(query.getPostName())) {
            predicate.and(tb_1_.postName.contains(query.getPostName()));
        }
        // 岗位编码
        if (StringUtils.isNotBlank(query.getPostCode())) {
            predicate.and(tb_1_.postCode.eq(query.getPostCode()));
        }
        // 状态（0正常 1停用）
        if (StringUtils.isNotBlank(query.getStatus())) {
            predicate.and(tb_1_.status.contains(query.getStatus()));
        }
        return predicate;
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Flux<SysPost> selectListByQuery(SysPostQuery query) {
        R2DBCQuery<SysPost> r2dbcQuery = queryFactory.selectFrom(tb_1_)
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
    public Mono<SysPost> selectOneById(Long postId) {
        return queryFactory.selectFrom(tb_1_)
                .where(tb_1_.postId.eq(postId))
                .fetchOne();
    }

    /**
     * 根据岗位名称查询
     */
    @Override
    public Mono<SysPost> selectOneByPostName(String postName) {
        return queryFactory.selectFrom(tb_1_)
                .where(tb_1_.postName.eq(postName))
                .fetchOne();
    }

    /**
     * 根据岗位编码查询
     */
    @Override
    public Mono<SysPost> selectOneByPostCode(String postCode) {
        return queryFactory.selectFrom(tb_1_)
                .where(tb_1_.postCode.eq(postCode))
                .fetchOne();
    }

    /**
     * 新增
     */
    @Override
    public Mono<Long> insert(SysPost post) {
        return beforeInsert(post)
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
    public Mono<Long> updateById(SysPost post) {
        return beforeUpdate(post)
                .flatMap(entity -> {
                    // 执行修改
                    return queryFactory.update(tb_1_)
                            .populate(entity)
                            .where(tb_1_.postId.eq(entity.getPostId()))
                            .execute();
                });
    }

    /**
     * 根据主键查询
     */
    @Override
    public Flux<SysPost> selectListByIds(List<Long> postIds) {
        return queryFactory.selectFrom(tb_1_)
                .where(tb_1_.postId.in(postIds))
                .fetch();
    }

    /**
     * 根据主键批量删除
     */
    @Override
    public Mono<Long> deleteByIds(List<Long> postIds) {
        return queryFactory.delete(tb_1_)
                .where(tb_1_.postId.in(postIds))
                .execute();
    }

}

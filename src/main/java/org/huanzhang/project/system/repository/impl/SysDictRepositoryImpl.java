package org.huanzhang.project.system.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.r2dbc.R2DBCQuery;
import com.querydsl.r2dbc.R2DBCQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.huanzhang.common.constant.UserConstants;
import org.huanzhang.framework.security.ReactiveSecurityUtils;
import org.huanzhang.project.system.entity.SysDict;
import org.huanzhang.project.system.entity.impl.QSysDict;
import org.huanzhang.project.system.query.SysDictQuery;
import org.huanzhang.project.system.repository.SysDictRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 字典表 数据处理
 *
 * @author haihuoshu
 * @version 2025-12-11
 */
@Repository
@RequiredArgsConstructor
public class SysDictRepositoryImpl implements SysDictRepository {

    private static final QSysDict tb_1_ = new QSysDict("tb_1_");

    private final R2DBCQueryFactory queryFactory;

    /**
     * 根据条件查询字典数量
     */
    @Override
    public Mono<Long> selectCountByQuery(SysDictQuery query) {
        return queryFactory.selectFrom(tb_1_)
                .where(buildPredicate(query))
                .fetchCount();
    }

    private Predicate buildPredicate(SysDictQuery query) {
        BooleanBuilder predicate = new BooleanBuilder();
        // 字典类型
        if (StringUtils.isNotBlank(query.getDictType())) {
            predicate.and(tb_1_.dictType.eq(query.getDictType()));
        }
        // 字典值
        if (StringUtils.isNotBlank(query.getDictValue())) {
            predicate.and(tb_1_.dictValue.contains(query.getDictValue()));
        }
        // 字典标签
        if (StringUtils.isNotBlank(query.getDictLabel())) {
            predicate.and(tb_1_.dictValue.contains(query.getDictLabel()));
        }
        // 状态
        if (StringUtils.isNotBlank(query.getStatus())) {
            predicate.and(tb_1_.status.eq(query.getStatus()));
        }
        return predicate;
    }

    /**
     * 根据条件查询字典列表
     */
    @Override
    public Flux<SysDict> selectListByQuery(SysDictQuery query) {
        R2DBCQuery<SysDict> r2dbcQuery = queryFactory.selectFrom(tb_1_)
                .where(buildPredicate(query));

        if (ObjectUtils.allNotNull(query.getPageNum(), query.getPageSize())) {
            PageRequest pageable = PageRequest.of(query.getPageNum() - 1, query.getPageSize());
            r2dbcQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
        }

        return r2dbcQuery.fetch();
    }

    /**
     * 根据字典类型查询字典列表
     */
    @Override
    public Flux<SysDict> selectListByType(String dictType) {
        return queryFactory.selectFrom(tb_1_)
                .where(tb_1_.dictType.eq(dictType)
                        .and(tb_1_.status.eq(UserConstants.NORMAL))
                )
                .orderBy(tb_1_.dictSort.asc())
                .fetch();
    }

    /**
     * 根据字典ID查询一条
     */
    @Override
    public Mono<SysDict> selectOneById(Long dictId) {
        return queryFactory.selectFrom(tb_1_)
                .where(tb_1_.dictId.eq(dictId))
                .fetchOne();
    }

    /**
     * 根据字典类型和字典值查询一条数据
     */
    @Override
    public Mono<SysDict> selectOneByTypeAndValue(String dictType, String dictValue) {
        return queryFactory.selectFrom(tb_1_)
                .where(tb_1_.dictType.eq(dictType)
                        .and(tb_1_.dictValue.eq(dictValue))
                )
                .fetchOne();
    }

    /**
     * 新增字典
     */
    @Override
    public Mono<Long> insertDict(SysDict entity) {
        return ReactiveSecurityUtils.getUsername()
                .flatMap(username -> {
                    entity.setCreateBy(username);
                    entity.setCreateTime(LocalDateTime.now());
                    entity.setUpdateBy(entity.getCreateBy());
                    entity.setUpdateTime(entity.getCreateTime());
                    return queryFactory.insert(tb_1_)
                            .populate(entity)
                            .execute();
                });
    }

    /**
     * 修改字典
     */
    @Override
    public Mono<Long> updateDict(SysDict entity) {
        return ReactiveSecurityUtils.getUsername()
                .flatMap(username -> {
                    entity.setUpdateBy(username);
                    entity.setUpdateTime(LocalDateTime.now());
                    return queryFactory.update(tb_1_)
                            .populate(entity)
                            .where(tb_1_.dictId.eq(entity.getDictId()))
                            .execute();
                });
    }

    /**
     * 同步修改字典类型
     */
    @Override
    public Mono<Long> updateDictType(String oldDictType, String newDictType) {
        return ReactiveSecurityUtils.getUsername()
                .flatMap(username -> {
                    // 执行修改
                    return queryFactory.update(tb_1_)
                            .set(tb_1_.dictType, newDictType)
                            .set(tb_1_.updateBy, username)
                            .set(tb_1_.updateTime, LocalDateTime.now())
                            .where(tb_1_.dictType.eq(oldDictType))
                            .execute();
                });
    }

    /**
     * 根据字典ID查询列表
     */
    @Override
    public Flux<SysDict> selectListByDictIds(List<Long> dictIds) {
        return queryFactory.selectFrom(tb_1_)
                .where(tb_1_.dictId.in(dictIds))
                .fetch();
    }

    /**
     * 根据字典类型查询字典数量
     */
    @Override
    public Mono<Long> selectCountByType(String dictType) {
        return queryFactory.selectFrom(tb_1_)
                .where(tb_1_.dictType.eq(dictType))
                .limit(1)
                .fetchCount();
    }

    /**
     * 通过字典ID删除
     */
    @Override
    public Mono<Long> deleteById(Long dictId) {
        return queryFactory.delete(tb_1_)
                .where(tb_1_.dictId.eq(dictId))
                .execute();
    }
}

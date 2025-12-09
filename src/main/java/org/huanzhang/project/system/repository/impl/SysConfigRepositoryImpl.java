package org.huanzhang.project.system.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.r2dbc.R2DBCQuery;
import com.querydsl.r2dbc.R2DBCQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.huanzhang.framework.security.ReactiveSecurityUtils;
import org.huanzhang.project.system.entity.QSysConfig;
import org.huanzhang.project.system.entity.SysConfig;
import org.huanzhang.project.system.query.SysConfigQuery;
import org.huanzhang.project.system.repository.SysConfigRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 配置表 数据处理
 *
 * @author haihuoshu
 * @version 2025-12-08
 */
@Repository
@RequiredArgsConstructor
public class SysConfigRepositoryImpl implements SysConfigRepository {

    private final QSysConfig sysConfig = new QSysConfig("tb_1_");

    private final R2DBCQueryFactory queryFactory;

    /**
     * 根据条件查询配置数量
     */
    @Override
    public Mono<Long> selectCountByQuery(SysConfigQuery query) {
        return queryFactory.select(sysConfig)
                .from(sysConfig)
                .where(buildPredicate(query))
                .fetchCount();
    }

    private BooleanBuilder buildPredicate(SysConfigQuery query) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (StringUtils.isNotBlank(query.getConfigName())) {
            predicate.and(sysConfig.configName.contains(query.getConfigName()));
        }
        if (StringUtils.isNotBlank(query.getConfigKey())) {
            predicate.and(sysConfig.configKey.contains(query.getConfigKey()));
        }
        if (StringUtils.isNotBlank(query.getConfigType())) {
            predicate.and(sysConfig.configKey.eq(query.getConfigType()));
        }
        if (ObjectUtils.allNotNull(query.getStartTime())) {
            predicate.and(sysConfig.createTime.goe(LocalDateTime.of(query.getStartTime(), LocalTime.MIN)));
        }
        if (ObjectUtils.allNotNull(query.getEndTime())) {
            predicate.and(sysConfig.createTime.loe(LocalDateTime.of(query.getEndTime(), LocalTime.MAX)));
        }
        return predicate;
    }

    /**
     * 根据条件查询配置列表
     */
    @Override
    public Flux<SysConfig> selectListByQuery(SysConfigQuery query) {
        R2DBCQuery<SysConfig> r2dbcQuery = queryFactory.selectFrom(sysConfig)
                .where(buildPredicate(query));

        if (ObjectUtils.allNotNull(query.getPageNum(), query.getPageSize())) {
            PageRequest pageable = PageRequest.of(query.getPageNum() - 1, query.getPageSize());
            r2dbcQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
        }

        return r2dbcQuery.fetch();
    }

    /**
     * 根据配置ID查询配置列表
     */
    @Override
    public Flux<SysConfig> selectListByConfigIds(List<Long> configIds) {
        if (CollectionUtils.isNotEmpty(configIds)) {
            return queryFactory.selectFrom(sysConfig)
                    .where(sysConfig.configId.in(configIds))
                    .fetch();
        }
        return Flux.empty();
    }

    /**
     * 根据配置键查询一条数据
     */
    @Override
    public Mono<SysConfig> selectOneByConfigId(Long configId) {
        return queryFactory.selectFrom(sysConfig)
                .where(sysConfig.configId.eq(configId))
                .fetchOne();
    }

    /**
     * 根据配置键查询一条数据
     */
    @Override
    public Mono<SysConfig> selectOneByConfigKey(String configKey) {
        return queryFactory.selectFrom(sysConfig)
                .where(sysConfig.configKey.eq(configKey))
                .fetchOne();
    }

    /**
     * 新增配置
     */
    @Override
    public Mono<Long> insertConfig(SysConfig entity) {
        return ReactiveSecurityUtils.getUsername()
                .flatMap(username -> {
                    entity.setCreateBy(username);
                    entity.setCreateTime(LocalDateTime.now());
                    entity.setUpdateBy(entity.getCreateBy());
                    entity.setUpdateTime(entity.getCreateTime());
                    return queryFactory.insert(sysConfig)
                            .populate(entity)
                            .execute();
                });
    }

    /**
     * 修改配置
     */
    @Override
    public Mono<Long> updateConfig(SysConfig entity) {
        return ReactiveSecurityUtils.getUsername()
                .flatMap(username -> {
                    entity.setUpdateBy(username);
                    entity.setUpdateTime(LocalDateTime.now());
                    return queryFactory.update(sysConfig)
                            .populate(entity)
                            .where(sysConfig.configId.eq(entity.getConfigId()))
                            .execute();
                });
    }

    /**
     * 根据配置ID删除
     */
    @Override
    public Mono<Long> deleteByConfigId(Long configId) {
        return queryFactory.delete(sysConfig)
                .where(sysConfig.configId.eq(configId))
                .execute();
    }

}

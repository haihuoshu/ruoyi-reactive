package org.huanzhang.project.system.repository;

import org.huanzhang.project.system.entity.SysConfig;
import org.huanzhang.project.system.query.SysConfigQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 配置表 数据层
 *
 * @author haihuoshu
 * @version 2025-12-08
 */
public interface SysConfigRepository {

    /**
     * 根据条件查询配置数量
     */
    Mono<Long> selectCountByQuery(SysConfigQuery query);

    /**
     * 根据条件查询配置列表
     */
    Flux<SysConfig> selectListByQuery(SysConfigQuery query);

    /**
     * 根据配置ID查询配置列表
     */
    Flux<SysConfig> selectListByConfigIds(List<Long> configIds);

    /**
     * 根据配置ID查询一条数据
     */
    Mono<SysConfig> selectOneByConfigId(Long configId);

    /**
     * 根据配置键查询一条数据
     */
    Mono<SysConfig> selectOneByConfigKey(String configKey);

    /**
     * 新增配置
     */
    Mono<Long> insertConfig(SysConfig entity);

    /**
     * 修改配置
     */
    Mono<Long> updateConfig(SysConfig entity);

    /**
     * 根据配置ID删除
     */
    Mono<Long> deleteByConfigId(Long configId);

}

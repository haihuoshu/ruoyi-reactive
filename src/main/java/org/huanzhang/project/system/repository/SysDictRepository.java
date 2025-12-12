package org.huanzhang.project.system.repository;

import org.huanzhang.project.system.entity.SysDict;
import org.huanzhang.project.system.query.SysDictQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 字典表 数据层
 *
 * @author haihuoshu
 * @version 2025-12-11
 */
public interface SysDictRepository {

    /**
     * 根据条件查询字典数量
     */
    Mono<Long> selectCountByQuery(SysDictQuery query);

    /**
     * 根据条件查询字典列表
     */
    Flux<SysDict> selectListByQuery(SysDictQuery query);

    /**
     * 根据字典类型查询字典列表
     */
    Flux<SysDict> selectListByType(String dictType);

    /**
     * 根据字典ID查询一条
     */
    Mono<SysDict> selectOneById(Long dictId);

    /**
     * 根据字典类型和字典值查询
     */
    Mono<SysDict> selectOneByTypeAndValue(String dictType, String dictValue);

    /**
     * 新增字典
     */
    Mono<Long> insertDict(SysDict dictData);

    /**
     * 修改字典
     */
    Mono<Long> updateDict(SysDict dictData);

    /**
     * 同步修改字典类型
     */
    Mono<Long> updateDictType(String oldDictType, String newDictType);

    /**
     * 根据字典ID查询列表
     */
    Flux<SysDict> selectListByDictIds(List<Long> dictIds);

    /**
     * 根据字典类型查询字典数量
     */
    Mono<Long> selectCountByType(String dictType);

    /**
     * 通过字典ID删除
     */
    Mono<Long> deleteById(Long dictId);

}

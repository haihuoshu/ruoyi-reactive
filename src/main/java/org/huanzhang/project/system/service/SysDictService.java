package org.huanzhang.project.system.service;

import org.huanzhang.project.system.dto.SysDictInsertDTO;
import org.huanzhang.project.system.dto.SysDictUpdateDTO;
import org.huanzhang.project.system.query.SysDictQuery;
import org.huanzhang.project.system.vo.SysDictVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 字典表 业务层
 *
 * @author haihuoshu
 * @version 2025-12-11
 */
public interface SysDictService {

    /**
     * 根据条件查询字典数量
     */
    Mono<Long> selectDictCountByQuery(SysDictQuery query);

    /**
     * 根据条件查询字典列表
     */
    Flux<SysDictVO> selectDictListByQuery(SysDictQuery query);

    /**
     * 根据字典类型查询字典列表
     */
    Flux<SysDictVO> selectDictListByType(String dictType);

    /**
     * 根据字典ID查询详细信息
     */
    Mono<SysDictVO> selectDictById(Long dictId);

    /**
     * 新增字典
     */
    Mono<Void> insertDict(SysDictInsertDTO dto);

    /**
     * 修改字典
     */
    Mono<Void> updateDict(SysDictUpdateDTO dto);

    /**
     * 批量删除字典
     */
    Mono<Void> deleteDictByIds(List<Long> dictIds);

    /**
     * 刷新字典缓存
     */
    Mono<Void> refreshDictCache();

}

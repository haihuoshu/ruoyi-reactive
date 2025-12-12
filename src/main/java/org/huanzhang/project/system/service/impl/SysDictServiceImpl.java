package org.huanzhang.project.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.huanzhang.common.exception.ServiceException;
import org.huanzhang.common.utils.DictUtils;
import org.huanzhang.project.system.converter.SysDictMapper;
import org.huanzhang.project.system.dto.SysDictInsertDTO;
import org.huanzhang.project.system.dto.SysDictUpdateDTO;
import org.huanzhang.project.system.entity.SysDict;
import org.huanzhang.project.system.query.SysDictQuery;
import org.huanzhang.project.system.repository.SysDictRepository;
import org.huanzhang.project.system.service.SysDictService;
import org.huanzhang.project.system.vo.SysDictVO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

/**
 * 字典表 业务处理
 *
 * @author haihuoshu
 * @version 2025-12-11
 */
@Service
@RequiredArgsConstructor
public class SysDictServiceImpl implements SysDictService {

    private final SysDictRepository sysDictRepository;
    private final SysDictMapper sysDictMapper;

    /**
     * 根据条件查询字典数量
     */
    @Override
    public Mono<Long> selectDictCountByQuery(SysDictQuery query) {
        return sysDictRepository.selectCountByQuery(query);
    }

    /**
     * 根据条件查询字典列表
     */
    @Override
    public Flux<SysDictVO> selectDictListByQuery(SysDictQuery query) {
        return sysDictRepository.selectListByQuery(query)
                .map(sysDictMapper::toVo);
    }

    /**
     * 根据字典类型查询字典列表
     */
    @Override
    public Flux<SysDictVO> selectDictListByType(String dictType) {
        return DictUtils.getDictCache(dictType)
                .switchIfEmpty(sysDictRepository.selectListByType(dictType)
                        .collectList()
                        .flatMapMany(list -> DictUtils.setDictCache(dictType, list)
                                .thenMany(Flux.fromIterable(list))
                        )
                )
                .map(sysDictMapper::toVo);
    }

    /**
     * 根据字典ID查询详细信息
     */
    @Override
    public Mono<SysDictVO> selectDictById(Long dictId) {
        return sysDictRepository.selectOneById(dictId)
                .switchIfEmpty(ServiceException.monoInstance("字典不存在"))
                .map(sysDictMapper::toVo);
    }

    /**
     * 新增字典
     */
    @Override
    public Mono<Void> insertDict(SysDictInsertDTO dto) {
        SysDict entity = sysDictMapper.toEntity(dto);
        return checkDeptNameUnique(entity)
                .then(sysDictRepository.insertDict(entity))
                .thenMany(sysDictRepository.selectListByType(entity.getDictType())
                        .collectList()
                        .flatMapMany(list -> DictUtils.setDictCache(entity.getDictType(), list))
                )
                .then();
    }

    /**
     * 检查字典值是否唯一
     */
    private Mono<Void> checkDeptNameUnique(SysDict dept) {
        return sysDictRepository.selectOneByTypeAndValue(dept.getDictType(), dept.getDictValue())
                .flatMap(info -> {
                    if (ObjectUtils.notEqual(info.getDictId(), dept.getDictId())) {
                        return ServiceException.monoInstance("字典值已存在");
                    }
                    return Mono.empty();
                })
                .then();
    }

    /**
     * 修改字典
     */
    @Override
    public Mono<Void> updateDict(SysDictUpdateDTO dto) {
        SysDict entity = sysDictMapper.toEntity(dto);

        return checkDeptNameUnique(entity)
                .then(sysDictRepository.selectOneById(dto.getDictId())
                        .switchIfEmpty(ServiceException.monoInstance("字典不存在")))
                .flatMap(dict ->
                        sysDictRepository.updateDict(entity)
                                .then(updateDictType(dto, dict))
                )
                .then();
    }

    private Mono<Void> updateDictType(SysDictUpdateDTO dto, SysDict dict) {
        if (Objects.equals(dto.getDictType(), "sys_dict_type")) {
            if (ObjectUtils.notEqual(dict.getDictValue(), dto.getDictValue())) {
                return sysDictRepository.updateDictType(dict.getDictValue(), dto.getDictValue())
                        .then(updateDictCache(dict.getDictValue(), dto.getDictValue()));
            }
        }
        return updateDictCache(dict.getDictType(), dto.getDictType());
    }

    private Mono<Void> updateDictCache(String oldDictType, String newDictType) {
        if (ObjectUtils.notEqual(oldDictType, newDictType)) {
            return updateDictCache(oldDictType)
                    .then(updateDictCache(newDictType));
        }

        return updateDictCache(newDictType);
    }

    private Mono<Void> updateDictCache(String dictType) {
        return sysDictRepository.selectListByType(dictType)
                .collectList()
                .flatMapMany(dictList -> DictUtils.setDictCache(dictType, dictList))
                .then();
    }


    /**
     * 批量删除字典
     */
    @Override
    public Mono<Void> deleteDictByIds(List<Long> dictIds) {
        return sysDictRepository.selectListByDictIds(dictIds)
                .flatMap(dict -> {
                    if (Objects.equals(dict.getDictType(), "sys_dict_type")) {
                        return sysDictRepository.selectCountByType(dict.getDictValue())
                                .flatMap(count -> {
                                    if (count > 0) {
                                        return ServiceException.monoInstance(String.format("%1$s已分配,不能删除", dict.getDictLabel()));
                                    }
                                    return Mono.empty();
                                });
                    }

                    return sysDictRepository.deleteById(dict.getDictId())
                            .then(Mono.defer(() -> {
                                // 如果删除的是字典类型，则删除字典缓存
                                if (Objects.equals(dict.getDictType(), "sys_dict_type")) {
                                    return DictUtils.removeDictCache(dict.getDictValue());
                                }

                                // 更新字典缓存
                                return updateDictCache(dict.getDictType());
                            }));
                })
                .then();
    }

    /**
     * 刷新字典缓存
     */
    @Override
    public Mono<Void> refreshDictCache() {
        return DictUtils.clearDictCache()
                .thenMany(sysDictRepository.selectListByType("sys_dict_type"))
                .flatMap(dict -> updateDictCache(dict.getDictValue()))
                .then();
    }

}

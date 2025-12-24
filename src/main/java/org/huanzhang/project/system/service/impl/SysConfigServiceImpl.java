package org.huanzhang.project.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.huanzhang.common.constant.CacheConstants;
import org.huanzhang.common.constant.UserConstants;
import org.huanzhang.common.exception.ServiceException;
import org.huanzhang.common.utils.IdgenUtils;
import org.huanzhang.framework.redis.ReactiveRedisUtils;
import org.huanzhang.project.system.converter.SysConfigMapper;
import org.huanzhang.project.system.dto.SysConfigInsertDTO;
import org.huanzhang.project.system.dto.SysConfigUpdateDTO;
import org.huanzhang.project.system.entity.SysConfig;
import org.huanzhang.project.system.query.SysConfigQuery;
import org.huanzhang.project.system.repository.SysConfigRepository;
import org.huanzhang.project.system.service.SysConfigService;
import org.huanzhang.project.system.vo.SysConfigVO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * 配置表 业务处理
 *
 * @author haihuoshu
 * @version 2025-12-08
 */
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl implements SysConfigService {

    private final SysConfigRepository sysConfigRepository;
    private final SysConfigMapper sysConfigMapper;

    private final ReactiveRedisUtils<String> reactiveRedisUtils;

    /**
     * 根据条件查询配置列表
     */
    @Override
    public Flux<SysConfigVO> selectConfigList(SysConfigQuery query) {
        return sysConfigRepository.selectListByQuery(query)
                .map(sysConfigMapper::toVo);
    }

    /**
     * 根据条件查询配置数量
     */
    @Override
    public Mono<Long> selectConfigCount(SysConfigQuery query) {
        return sysConfigRepository.selectCountByQuery(query);
    }

    /**
     * 根据配置ID查询详细信息
     */
    @Override
    public Mono<SysConfigVO> selectConfigById(Long configId) {
        return sysConfigRepository.selectOneByConfigId(configId)
                .switchIfEmpty(ServiceException.monoInstance("配置不存在"))
                .map(sysConfigMapper::toVo);
    }

    /**
     * 根据配置键查询配置值
     */
    @Override
    public Mono<String> selectConfigByKey(String configKey) {
        // 先从redis中读取
        return reactiveRedisUtils.getCacheObject(getCacheKey(configKey))
                .switchIfEmpty(Mono.defer(() -> {
                    // 查询数据库
                    return sysConfigRepository.selectOneByConfigKey(configKey)
                            .flatMap(sysConfig -> {
                                // 存入redis中
                                return reactiveRedisUtils.setCacheObject(getCacheKey(configKey), sysConfig.getConfigValue(), Duration.ofDays(7))
                                        .thenReturn(sysConfig.getConfigValue());
                            });
                }));
    }

    /**
     * 获取验证码开关
     */
    @Override
    public Mono<Boolean> selectCaptchaEnabled() {
        return selectConfigByKey("sys.account.captchaEnabled")
                .map(BooleanUtils::toBooleanObject)
                .defaultIfEmpty(Boolean.FALSE);
    }

    /**
     * 新增配置
     */
    @Override
    public Mono<Void> insertConfig(SysConfigInsertDTO dto) {
        SysConfig entity = sysConfigMapper.toEntity(dto);
        entity.setConfigId(IdgenUtils.nextId());
        entity.setConfigType(UserConstants.NO);

        return this.checkConfigKeyExists(entity)
                .flatMap(configKeyExists -> {
                    if (configKeyExists) {
                        return ServiceException.monoInstance("新增配置'" + dto.getConfigName() + "'失败，配置键已存在");
                    }
                    return sysConfigRepository.insertConfig(entity)
                            .then(reactiveRedisUtils.setCacheObject(getCacheKey(dto.getConfigKey()), dto.getConfigValue(), Duration.ofDays(7)));
                })
                .then();
    }

    /**
     * 检查配置键是否存在
     */
    private Mono<Boolean> checkConfigKeyExists(SysConfig config) {
        return sysConfigRepository.selectOneByConfigKey(config.getConfigKey())
                .map(info -> ObjectUtils.notEqual(info.getConfigId(), config.getConfigId()))
                .defaultIfEmpty(Boolean.FALSE);
    }

    /**
     * 修改配置
     */
    @Override
    public Mono<Void> updateConfig(SysConfigUpdateDTO dto) {
        SysConfig entity = sysConfigMapper.toEntity(dto);

        return this.checkConfigKeyExists(entity)
                .flatMap(configKeyExists -> {
                    if (configKeyExists) {
                        return ServiceException.monoInstance("修改配置'" + dto.getConfigName() + "'失败，配置键已存在");
                    }
                    return sysConfigRepository.selectOneByConfigId(dto.getConfigId())
                            .switchIfEmpty(ServiceException.monoInstance("配置不存在"))
                            .flatMap(temp -> {
                                // 修改
                                return sysConfigRepository.updateConfig(entity)
                                        .then(Mono.defer(() -> {
                                            Mono<Long> deleteObject = Mono.empty();
                                            if (ObjectUtils.notEqual(temp.getConfigKey(), dto.getConfigKey())) {
                                                deleteObject = reactiveRedisUtils.deleteCache(getCacheKey(temp.getConfigKey()));
                                            }
                                            return deleteObject.then(reactiveRedisUtils.setCacheObject(getCacheKey(dto.getConfigKey()), dto.getConfigValue(), Duration.ofDays(7)));
                                        }));
                            });
                })
                .then();
    }

    /**
     * 批量删除配置
     */
    @Override
    public Mono<Void> deleteConfigByIds(List<Long> configIds) {
        return sysConfigRepository.selectListByConfigIds(configIds)
                .flatMap(config -> {
                    if (Objects.equals(UserConstants.YES, config.getConfigType())) {
                        return ServiceException.monoInstance(String.format("内置配置【%1$s】不能删除 ", config.getConfigKey()));
                    }

                    return sysConfigRepository.deleteByConfigId(config.getConfigId())
                            .then(reactiveRedisUtils.deleteCache(getCacheKey(config.getConfigKey())));
                })
                .then();
    }

    /**
     * 刷新配置缓存
     */
    @Override
    public Mono<Void> refreshConfigCache() {
        return reactiveRedisUtils.cacheKeys(CacheConstants.SYS_CONFIG_KEY + "*")
                .transform(reactiveRedisUtils::deleteCache)
                .thenMany(sysConfigRepository.selectListByQuery(new SysConfigQuery()))
                .map(config -> reactiveRedisUtils.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue(), Duration.ofDays(7)))
                .then();
    }

    /**
     * 获取配置缓存键
     */
    private String getCacheKey(String configKey) {
        return CacheConstants.SYS_CONFIG_KEY + configKey;
    }

}

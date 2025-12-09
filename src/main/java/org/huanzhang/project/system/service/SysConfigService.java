package org.huanzhang.project.system.service;

import org.huanzhang.project.system.dto.SysConfigInsertDTO;
import org.huanzhang.project.system.dto.SysConfigUpdateDTO;
import org.huanzhang.project.system.query.SysConfigQuery;
import org.huanzhang.project.system.vo.SysConfigVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 配置表 业务层
 *
 * @author haihuoshu
 * @version 2025-12-08
 */
public interface SysConfigService {

    /**
     * 根据条件查询配置列表
     */
    Flux<SysConfigVO> selectConfigList(SysConfigQuery query);

    /**
     * 根据条件查询配置数量
     */
    Mono<Long> selectConfigCount(SysConfigQuery query);

    /**
     * 根据配置ID查询详细信息
     */
    Mono<SysConfigVO> selectConfigById(Long configId);

    /**
     * 根据配置键查询配置值
     */
    Mono<String> selectConfigByKey(String configKey);

    /**
     * 获取验证码开关
     */
    Mono<Boolean> selectCaptchaEnabled();

    /**
     * 新增配置
     */
    Mono<Void> insertConfig(SysConfigInsertDTO dto);

    /**
     * 修改配置
     */
    Mono<Void> updateConfig(SysConfigUpdateDTO dto);

    /**
     * 批量删除配置
     */
    Mono<Void> deleteConfigByIds(List<Long> configIds);

    /**
     * 刷新配置缓存
     */
    Mono<Void> refreshConfigCache();

}

package org.huanzhang.common.utils;

import org.huanzhang.common.constant.CacheConstants;
import org.huanzhang.framework.redis.ReactiveRedisUtils;
import org.huanzhang.project.system.entity.SysDict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

/**
 * 字典工具类
 *
 * @author ruoyi
 */
@Component
public class DictUtils {

    private static ReactiveRedisUtils<SysDict> reactiveRedisUtils;

    /**
     * 分隔符
     */
    public static final String SEPARATOR = ",";

    @Autowired
    public void setReactiveRedisUtils(ReactiveRedisUtils<SysDict> reactiveRedisUtils) {
        DictUtils.reactiveRedisUtils = reactiveRedisUtils;
    }

    /**
     * 设置字典缓存
     */
    public static Mono<Boolean> setDictCache(String key, List<SysDict> dictDatas) {
        return reactiveRedisUtils.setCacheList(getCacheKey(key), dictDatas, Duration.ofDays(7));
    }

    /**
     * 获取字典缓存
     */
    public static Flux<SysDict> getDictCache(String key) {
        return reactiveRedisUtils.getCacheList(getCacheKey(key));
    }

    /**
     * 删除指定字典缓存
     */
    public static Mono<Long> removeDictCache(String key) {
        return reactiveRedisUtils.deleteCache(getCacheKey(key));
    }

    /**
     * 清空字典缓存
     */
    public static Mono<Void> clearDictCache() {
        return reactiveRedisUtils.cacheKeys(CacheConstants.SYS_DICT_KEY + "*")
                .transform(reactiveRedisUtils::deleteCache)
                .then();
    }

    /**
     * 设置cache key
     *
     * @param configKey 参数键
     * @return 缓存键key
     */
    public static String getCacheKey(String configKey) {
        return CacheConstants.SYS_DICT_KEY + configKey;
    }
}

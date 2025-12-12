package org.huanzhang.common.utils;

import org.huanzhang.common.constant.CacheConstants;
import org.huanzhang.common.utils.spring.SpringUtils;
import org.huanzhang.framework.redis.ReactiveRedisUtils;
import org.huanzhang.framework.redis.RedisCache;
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
    public static Flux<Void> setDictCache(String key, List<SysDict> dictDatas) {
        return reactiveRedisUtils.setCacheList(getCacheKey(key), dictDatas, Duration.ofDays(7));
    }

    /**
     * 获取字典缓存
     */
    public static Flux<SysDict> getDictCache(String key) {
        return reactiveRedisUtils.getCacheList(getCacheKey(key));
    }

    /**
     * 获取字典缓存
     *
     * @param key 参数键
     * @return dictDatas 字典数据列表
     */
    public static List<SysDict> getDictCache2(String key) {
        List<SysDict> arrayCache = SpringUtils.getBean(RedisCache.class).getCacheList(getCacheKey(key));
        if (StringUtils.isNotNull(arrayCache)) {
            return arrayCache;
        }
        return null;
    }

    /**
     * 根据字典类型和字典值获取字典标签
     *
     * @param dictType  字典类型
     * @param dictValue 字典值
     * @param separator 分隔符
     * @return 字典标签
     */
    public static String getDictLabel(String dictType, String dictValue, String separator) {
        StringBuilder propertyString = new StringBuilder();
        List<SysDict> datas = getDictCache2(dictType);
        if (StringUtils.isNull(datas)) {
            return StringUtils.EMPTY;
        }
        if (StringUtils.containsAny(separator, dictValue)) {
            for (SysDict dict : datas) {
                for (String value : dictValue.split(separator)) {
                    if (value.equals(dict.getDictValue())) {
                        propertyString.append(dict.getDictLabel()).append(separator);
                        break;
                    }
                }
            }
        } else {
            for (SysDict dict : datas) {
                if (dictValue.equals(dict.getDictValue())) {
                    return dict.getDictLabel();
                }
            }
        }
        return StringUtils.stripEnd(propertyString.toString(), separator);
    }

    /**
     * 根据字典类型和字典标签获取字典值
     *
     * @param dictType  字典类型
     * @param dictLabel 字典标签
     * @param separator 分隔符
     * @return 字典值
     */
    public static String getDictValue(String dictType, String dictLabel, String separator) {
        StringBuilder propertyString = new StringBuilder();
        List<SysDict> datas = getDictCache2(dictType);
        if (StringUtils.isNull(datas)) {
            return StringUtils.EMPTY;
        }
        if (StringUtils.containsAny(separator, dictLabel)) {
            for (SysDict dict : datas) {
                for (String label : dictLabel.split(separator)) {
                    if (label.equals(dict.getDictLabel())) {
                        propertyString.append(dict.getDictValue()).append(separator);
                        break;
                    }
                }
            }
        } else {
            for (SysDict dict : datas) {
                if (dictLabel.equals(dict.getDictLabel())) {
                    return dict.getDictValue();
                }
            }
        }
        return StringUtils.stripEnd(propertyString.toString(), separator);
    }

    /**
     * 根据字典类型获取字典所有标签
     *
     * @param dictType 字典类型
     * @return 字典值
     */
    public static String getDictLabels(String dictType) {
        StringBuilder propertyString = new StringBuilder();
        List<SysDict> datas = getDictCache2(dictType);
        if (StringUtils.isNull(datas)) {
            return StringUtils.EMPTY;
        }
        for (SysDict dict : datas) {
            propertyString.append(dict.getDictLabel()).append(SEPARATOR);
        }
        return StringUtils.stripEnd(propertyString.toString(), SEPARATOR);
    }

    /**
     * 删除指定字典缓存
     */
    public static Mono<Boolean> removeDictCache(String key) {
        return reactiveRedisUtils.deleteObject(getCacheKey(key));
    }

    /**
     * 清空字典缓存
     */
    public static Mono<Void> clearDictCache() {
        return reactiveRedisUtils.keys(CacheConstants.SYS_DICT_KEY + "*")
                .transform(reactiveRedisUtils::deleteObject)
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

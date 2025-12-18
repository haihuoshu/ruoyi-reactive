package org.huanzhang.framework.redis;

import jakarta.annotation.Resource;
import org.reactivestreams.Publisher;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveListOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collection;

/**
 * 响应式redis工具类
 *
 * @author haihuoshu
 * @version 2025-12-18
 */
@Component
public class ReactiveRedisUtils<T> {

    @Resource
    private ReactiveRedisOperations<String, T> reactiveRedisOperations;

    /**
     * 获得缓存key
     */
    public Flux<String> cacheKeys(String pattern) {
        return reactiveRedisOperations.keys(pattern);
    }

    /**
     * 删除缓存
     */
    public Mono<Long> deleteCache(String... key) {
        return reactiveRedisOperations.delete(key);
    }

    /**
     * 删除缓存
     */
    public Mono<Long> deleteCache(Publisher<String> keys) {
        return reactiveRedisOperations.delete(keys);
    }

    /**
     * 获得缓存的基本对象。
     */
    public Mono<T> getCacheObject(String key) {
        ReactiveValueOperations<String, T> opsForValue = reactiveRedisOperations.opsForValue();
        return opsForValue.get(key);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     */
    public Mono<Boolean> setCacheObject(String key, T value, Duration timeout) {
        ReactiveValueOperations<String, T> opsForValue = reactiveRedisOperations.opsForValue();
        return opsForValue.set(key, value, timeout);
    }

    /**
     * 缓存list数据
     */
    public Mono<Boolean> setCacheList(String key, Collection<T> values, Duration timeout) {
        ReactiveListOperations<String, T> opsForList = reactiveRedisOperations.opsForList();
        return opsForList.rightPushAll(key, values)
                .then(reactiveRedisOperations.expire(key, timeout));
    }

    /**
     * 获取缓存的list数据
     */
    public Flux<T> getCacheList(String key) {
        ReactiveListOperations<String, T> opsForList = reactiveRedisOperations.opsForList();
        return opsForList.range(key, 0, -1);
    }

    /**
     * 往Hash中存入数据
     */
    public Mono<Boolean> setCacheMapValue(String key, String hashKey, T value, Duration timeout) {
        ReactiveHashOperations<String, String, T> opsForList = reactiveRedisOperations.opsForHash();
        return opsForList.put(key, hashKey, value)
                .then(reactiveRedisOperations.expire(key, timeout));
    }

    /**
     * 获取Hash中的数据
     */
    public Mono<T> getCacheMapValue(String key, String hashKey) {
        ReactiveHashOperations<String, String, T> opsForList = reactiveRedisOperations.opsForHash();
        return opsForList.get(key, hashKey);
    }
}

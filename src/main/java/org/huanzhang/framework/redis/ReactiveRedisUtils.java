package org.huanzhang.framework.redis;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component
public class ReactiveRedisUtils<T> {

    @Resource
    public RedisTemplate<String, T> redisTemplate;

    /**
     * 获得缓存的基本对象列表
     */
    public Flux<String> keys(final String pattern) {
        return Flux.fromIterable(redisTemplate.keys(pattern));
    }

    /**
     * 删除单个对象
     */
    public Mono<Boolean> deleteObject(final String key) {
        return Mono.justOrEmpty(redisTemplate.delete(key));
    }

    /**
     * 删除单个对象
     */
    public Mono<Void> deleteObject(Flux<String> key) {
        return key.collectList()
                .map(a -> redisTemplate.delete(a))
                .then();
    }

    /**
     * 获得缓存的基本对象。
     */
    public Mono<T> getCacheObject(final String key) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return Mono.justOrEmpty(operation.get(key));
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     */
    public Mono<Void> setCacheObject(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
        return Mono.empty();
    }

    /**
     * 缓存list数据
     */
    public Flux<Void> setCacheList(String key, Collection<T> values, Duration duration) {
        redisTemplate.opsForList().rightPushAll(key, values);
        redisTemplate.expire(key, duration);
        return Flux.empty();
    }

    /**
     * 获取缓存的list数据
     */
    public Flux<T> getCacheList(String key) {
        List<T> list = redisTemplate.opsForList().range(key, 0, -1);
        if (Objects.isNull(list)) return Flux.empty();
        return Flux.fromIterable(list);
    }

}

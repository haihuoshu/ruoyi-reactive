package org.huanzhang.framework.interceptor.impl;

import jakarta.annotation.Resource;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.huanzhang.common.constant.CacheConstants;
import org.huanzhang.framework.interceptor.RepeatSubmitInterceptor;
import org.huanzhang.framework.interceptor.annotation.RepeatSubmit;
import org.huanzhang.framework.redis.ReactiveRedisUtils;
import org.huanzhang.framework.webflux.utils.WebFluxUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;

/**
 * 判断请求url和数据是否和上一次相同，
 * 如果和上次相同，则是重复提交表单。 有效时间为10秒内。
 *
 * @author ruoyi
 */
@Component
public class SameUrlDataInterceptor extends RepeatSubmitInterceptor {

    /**
     * 重复提交数据传输对象，存储请求的参数、Body和时间戳
     */
    @Data
    public static class RepeatSubmitDTO implements Serializable {

        private String params; // 请求参数
        private String body;   // 请求体
        private Long timestamp; // 请求时间戳（替换原time，语义更清晰）
    }

    @Value("${token.header:Authorization}")
    private String tokenHeader;

    @Resource
    private ReactiveRedisUtils<RepeatSubmitDTO> reactiveRedisUtils;

    /**
     * 判断是否为重复提交
     */
    @Override
    public Mono<Boolean> isRepeatSubmit(ServerHttpRequest request, RepeatSubmit annotation) {
        // 读取请求体
        return WebFluxUtils.readBodyAsString(request)
                .map(body -> {
                    RepeatSubmitDTO dto = new RepeatSubmitDTO();
                    // 读取请求参数
                    dto.setParams(WebFluxUtils.readParamsAsString(request));
                    dto.setBody(body);
                    dto.setTimestamp(System.currentTimeMillis());
                    return dto;
                })
                // 读取body可能耗时，切换到IO线程池
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(newDto -> {
                    // 请求地址（作为存放cache的key值）
                    String url = request.getURI().getPath();

                    // 唯一值（没有消息头则使用请求地址）
                    String token = StringUtils.trimToEmpty(request.getHeaders().getFirst(tokenHeader));

                    // 唯一标识（指定key + url + 消息头）
                    String cacheKey = CacheConstants.REPEAT_SUBMIT_KEY + url + token;

                    // 从Redis获取历史请求数据
                    return reactiveRedisUtils.getCacheMapValue(cacheKey, url)
                            .map(oldDto -> {
                                // 对比新老请求数据，判断是否为重复提交
                                return Objects.equals(newDto.getParams(), oldDto.getParams())
                                        && Objects.equals(newDto.getBody(), oldDto.getBody())
                                        && (newDto.getTimestamp() - oldDto.getTimestamp()) < annotation.interval();
                            })
                            .defaultIfEmpty(false)
                            .flatMap(isRepeat -> {
                                // 缓存新的请求数据
                                return reactiveRedisUtils.setCacheMapValue(cacheKey, url, newDto, Duration.ofSeconds(annotation.interval()))
                                        // 缓存完成后，返回是否重复的结果
                                        .thenReturn(isRepeat);
                            });
                });
    }

}

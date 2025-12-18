package org.huanzhang.framework.redis;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 响应式redis配置
 *
 * @author haihuoshu
 * @version 2025-12-18
 */
@Configuration
@EnableCaching
public class ReactiveRedisConfiguration implements CachingConfigurer {

    @Bean
    public ReactiveRedisOperations<String, Object> reactiveRedisOperations(ReactiveRedisConnectionFactory connectionFactory) {
        // 配置Jackson的序列化规则
        ObjectMapper objectMapper = new ObjectMapper();
        // 支持Java 8时间类型（LocalDateTime、LocalDate等）
        objectMapper.registerModule(new JavaTimeModule());
        // 保留类型信息（反序列化时能正确识别对象的实际类型，比如List中的泛型、子类对象）
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        // 定义String序列化器（用于key的序列化）
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        // 定义Jackson序列化器（用于value的序列化）
        Jackson2JsonRedisSerializer<Object> jacksonSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        // 构建序列化上下文，指定key和value的序列化器
        RedisSerializationContext<String, Object> serializationContext = RedisSerializationContext.<String, Object>newSerializationContext()
                .key(stringSerializer)
                .value(jacksonSerializer)
                .hashKey(stringSerializer)
                .hashValue(jacksonSerializer)
                .build();

        // 创建并返回ReactiveRedisTemplate
        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }

    /**
     * 限流脚本
     */
    @Bean
    public RedisScript<Long> limitScript() {
        String script = """
                local key = KEYS[1]
                local count = tonumber(ARGV[1])
                local time = tonumber(ARGV[2])
                local current = redis.call('get', key);
                if current and tonumber(current) > count then
                    return tonumber(current);
                end
                current = redis.call('incr', key)
                if tonumber(current) == 1 then
                    redis.call('expire', key, time)
                end
                return tonumber(current);
                """;

        return new DefaultRedisScript<>(script, Long.class);
    }

}

package org.huanzhang.framework.webflux.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class WebFluxUtils {

    private static ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        WebFluxUtils.objectMapper = objectMapper;
    }

    public static Mono<Void> writeBodyAsString(ServerHttpResponse response, Object object) {
        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        DataBuffer buffer = response.bufferFactory().wrap(bytes);

        return response.writeWith(Mono.just(buffer));
    }

    public static String readParamsAsString(ServerHttpRequest request) {
        try {
            return objectMapper.writeValueAsString(request.getQueryParams());
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    public static Mono<String> readBodyAsString(ServerHttpRequest request) {
        // 读取请求体（响应式方式，注意释放缓冲区）
        return DataBufferUtils.join(request.getBody())
                .map(buffer -> {
                    byte[] bytes = new byte[buffer.readableByteCount()];
                    buffer.read(bytes);
                    DataBufferUtils.release(buffer); // 释放缓冲区，避免内存泄漏
                    return new String(bytes, StandardCharsets.UTF_8);
                })
                .defaultIfEmpty("");
    }

}

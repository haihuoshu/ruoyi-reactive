package org.huanzhang.common.exception;

import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * 业务异常
 *
 * @author ruoyi
 */
@Data
@NoArgsConstructor
public final class ServiceException extends RuntimeException {

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误提示
     */
    private String message;

    public ServiceException(String message) {
        this.message = message;
    }

    public ServiceException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public static <T> Mono<T> monoInstance(String message) {
        return Mono.error(new ServiceException(message));
    }

}
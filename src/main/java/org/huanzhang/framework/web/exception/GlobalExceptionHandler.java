package org.huanzhang.framework.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.huanzhang.common.constant.HttpStatus;
import org.huanzhang.common.core.text.Convert;
import org.huanzhang.common.exception.DemoModeException;
import org.huanzhang.common.exception.ServiceException;
import org.huanzhang.common.utils.StringUtils;
import org.huanzhang.common.utils.html.EscapeUtil;
import org.huanzhang.framework.web.domain.AjaxResult;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.MissingRequestValueException;

import java.util.Objects;

/**
 * 全局异常处理器
 *
 * @author ruoyi
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 权限校验异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public AjaxResult handleAccessDeniedException(AccessDeniedException e, ServerHttpRequest request) {
        String requestURI = request.getURI().getPath();
        log.error("请求地址'{}',权限校验失败'{}'", requestURI, e.getMessage());
        return AjaxResult.error(HttpStatus.FORBIDDEN, "没有权限，请联系管理员授权");
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(MethodNotAllowedException.class)
    public AjaxResult handleHttpRequestMethodNotSupported(MethodNotAllowedException e,
                                                          ServerHttpRequest request) {
        String requestURI = request.getURI().getPath();
        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getHttpMethod());
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException.class)
    public AjaxResult handleServiceException(ServiceException e) {
        log.error(e.getMessage(), e);
        Integer code = e.getCode();
        return StringUtils.isNotNull(code) ? AjaxResult.error(code, e.getMessage()) : AjaxResult.error(e.getMessage());
    }

    /**
     * 请求路径中缺少必需的路径变量
     */
    @ExceptionHandler(MissingRequestValueException.class)
    public AjaxResult handleMissingPathVariableException(MissingRequestValueException e, ServerHttpRequest request) {
        String requestURI = request.getURI().getPath();
        log.error("请求路径中缺少必需的路径变量'{}',发生系统异常.", requestURI, e);
        return AjaxResult.error(String.format("请求路径中缺少必需的路径变量[%s]", e.getLabel()));
    }

    /**
     * 请求参数类型不匹配
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public AjaxResult handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, ServerHttpRequest request) {
        String requestURI = request.getURI().getPath();
        String value = Convert.toStr(e.getValue());
        if (StringUtils.isNotEmpty(value)) {
            value = EscapeUtil.clean(value);
        }
        log.error("请求参数类型不匹配'{}',发生系统异常.", requestURI, e);
        assert e.getRequiredType() != null;
        return AjaxResult.error(String.format("请求参数类型不匹配，参数[%s]要求类型为：'%s'，但输入值为：'%s'", e.getName(), e.getRequiredType().getName(), value));
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public AjaxResult handleRuntimeException(RuntimeException e, ServerHttpRequest request) {
        String requestURI = request.getURI().getPath();
        log.error("请求地址'{}',发生未知异常.", requestURI, e);
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public AjaxResult handleException(Exception e, ServerHttpRequest request) {
        String requestURI = request.getURI().getPath();
        log.error("请求地址'{}',发生系统异常.", requestURI, e);
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public AjaxResult handleBindException(BindException e) {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return AjaxResult.error(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        return AjaxResult.error(message);
    }

    /**
     * 演示模式异常
     */
    @ExceptionHandler(DemoModeException.class)
    public AjaxResult handleDemoModeException() {
        return AjaxResult.error("演示模式，不允许操作");
    }
}

package org.huanzhang.framework.web.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Schema(description = "AjaxResponse")
@Data
public class AjaxResponse<T> implements Serializable {

    @Schema(description = "状态码")
    private int code;

    @Schema(description = "消息内容")
    private String message;

    @Schema(description = "数据对象")
    private T data;

    private static <T> AjaxResponse<T> getInstance(int code, String message, T data) {
        AjaxResponse<T> response = new AjaxResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static AjaxResponse<Void> ok() {
        return ok(null);
    }

    public static <T> AjaxResponse<T> ok(T data) {
        return ok(data, "操作成功");
    }

    public static <T> AjaxResponse<T> ok(T data, String msg) {
        return getInstance(HttpStatus.OK.value(), msg, data);
    }

    public static AjaxResponse<Void> fail(String msg) {
        return fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    }

    public static AjaxResponse<Void> fail(int code, String msg) {
        return getInstance(code, msg, null);
    }

}

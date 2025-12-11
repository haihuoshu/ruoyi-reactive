package org.huanzhang.framework.web.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.huanzhang.common.constant.HttpStatus;

import java.io.Serializable;
import java.util.List;

@Schema(description = "ListResponse")
@Data
public class ListResponse<T> implements Serializable {

    @Schema(description = "状态码")
    private int code;

    @Schema(description = "消息内容")
    private String message;

    @Schema(description = "列表数据")
    private List<T> data;

    public static <T> ListResponse<T> getInstance(List<T> data) {
        ListResponse<T> response = new ListResponse<>();
        response.setCode(HttpStatus.SUCCESS);
        response.setMessage("查询成功");
        response.setData(data);
        return response;
    }

}
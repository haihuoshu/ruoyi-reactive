package org.huanzhang.framework.web.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.huanzhang.common.constant.HttpStatus;

import java.io.Serializable;
import java.util.List;

@Schema(description = "PageResponse")
@Data
public class PageResponse<T> implements Serializable {

    @Schema(description = "状态码")
    private int code;

    @Schema(description = "消息内容")
    private String message;

    @Schema(description = "列表数据")
    private List<T> data;

    @Schema(description = "总记录数")
    private long total;

    public static <T> PageResponse<T> getInstance(List<T> data, long total) {
        PageResponse<T> response = new PageResponse<>();
        response.setCode(HttpStatus.SUCCESS);
        response.setMessage("查询成功");
        response.setData(data);
        response.setTotal(total);
        return response;
    }

}
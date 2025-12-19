package org.huanzhang.project.system.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.huanzhang.framework.web.domain.PageQuery;

import java.time.LocalDate;

@Schema(description = "SysOperateLogQuery")
@Data
public class SysOperateLogQuery extends PageQuery {

    @Schema(description = "操作模块")
    private String title;

    @Schema(description = "业务类型（0其它 1新增 2修改 3删除）")
    private Integer businessType;

    @Schema(description = "操作人员")
    private String operName;

    @Schema(description = "操作地址")
    private String operIp;

    @Schema(description = "操作状态（0正常 1异常）")
    private Integer status;

    @Schema(description = "操作时间 start")
    private LocalDate startTime;

    @Schema(description = "操作时间 end")
    private LocalDate endTime;

}

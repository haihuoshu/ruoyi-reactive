package org.huanzhang.project.system.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.huanzhang.framework.web.domain.PageQuery;

import java.time.LocalDate;

@Schema(description = "SysAccessLogQuery")
@Data
public class SysAccessLogQuery extends PageQuery {

    @Schema(description = "用户账号")
    private String userName;

    @Schema(description = "登录状态（0成功 1失败）")
    private String status;

    @Schema(description = "登录IP地址")
    private String ipaddr;

    @Schema(description = "访问时间 start")
    private LocalDate startTime;

    @Schema(description = "访问时间 end")
    private LocalDate endTime;

}

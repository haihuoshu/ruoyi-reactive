package org.huanzhang.project.system.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.huanzhang.framework.web.domain.PageQuery;

import java.time.LocalDate;

@Schema(description = "SysConfigQuery")
@Data
public class SysConfigQuery extends PageQuery {

    @Schema(description = "配置名称")
    private String configName;

    @Schema(description = "配置键名")
    private String configKey;

    @Schema(description = "系统内置（Y是 N否）")
    private String configType;

    @Schema(description = "创建时间 start")
    private LocalDate startTime;

    @Schema(description = "创建时间 end")
    private LocalDate endTime;

}

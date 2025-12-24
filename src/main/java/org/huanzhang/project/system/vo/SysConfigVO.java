package org.huanzhang.project.system.vo;

import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(description = "SysConfigVO")
@Data
public class SysConfigVO implements Serializable {

    @Schema(description = "配置ID")
    @ExcelProperty("配置ID")
    private String configId;

    @Schema(description = "配置名称")
    @ExcelProperty("配置名称")
    private String configName;

    @Schema(description = "配置键")
    @ExcelProperty("配置键")
    private String configKey;

    @Schema(description = "配置值")
    @ExcelProperty("配置值")
    private String configValue;

    @Schema(description = "系统内置（Y是 N否）")
    @ExcelProperty("系统内置")
    private String configType;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "备注")
    @ExcelProperty("备注")
    private String remark;

}

package org.huanzhang.project.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.huanzhang.framework.aspectj.lang.annotation.Excel;
import org.huanzhang.framework.aspectj.lang.annotation.Excel.ColumnType;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(description = "SysConfigVO")
@Data
public class SysConfigVO implements Serializable {

    @Schema(description = "配置ID")
    @Excel(name = "配置ID", cellType = ColumnType.NUMERIC)
    private Long configId;

    @Schema(description = "配置名称")
    @Excel(name = "配置名称")
    private String configName;

    @Schema(description = "配置键")
    @Excel(name = "配置键")
    private String configKey;

    @Schema(description = "配置值")
    @Excel(name = "配置值")
    private String configValue;

    @Schema(description = "系统内置（Y是 N否）")
    @Excel(name = "系统内置", readConverterExp = "Y=是,N=否")
    private String configType;

    @Schema(description = "创建时间")
    @Excel(name = "创建时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "备注")
    @Excel(name = "备注")
    private String remark;

}

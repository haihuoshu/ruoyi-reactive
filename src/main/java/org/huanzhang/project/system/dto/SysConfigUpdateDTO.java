package org.huanzhang.project.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "SysConfigInsertDTO")
@Data
public class SysConfigUpdateDTO implements Serializable {

    @Schema(description = "配置ID")
    @NotNull(message = "配置ID不能为空")
    private Long configId;

    @Schema(description = "配置名称")
    @NotBlank(message = "配置名称不能为空")
    @Size(max = 100, message = "配置名称不能超过100个字符")
    private String configName;

    @Schema(description = "配置键")
    @NotBlank(message = "配置键长度不能为空")
    @Size(max = 100, message = "配置键不能超过100个字符")
    private String configKey;

    @Schema(description = "配置值")
    @NotBlank(message = "配置值不能为空")
    @Size(max = 500, message = "配置值不能超过500个字符")
    private String configValue;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注不能超过500个字符")
    private String remark;

}

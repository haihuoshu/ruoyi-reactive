package org.huanzhang.project.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "SysPostInsertDTO")
@Data
public class SysPostInsertDTO implements Serializable {

    @Schema(description = "岗位编码")
    @NotBlank(message = "岗位编码不能为空")
    @Size(max = 64, message = "岗位编码不能超过64个字符")
    private String postCode;

    @Schema(description = "岗位名称")
    @NotBlank(message = "岗位名称不能为空")
    @Size(max = 50, message = "岗位名称不能超过50个字符")
    private String postName;

    @Schema(description = "岗位排序")
    @NotNull(message = "显示顺序不能为空")
    private Integer postSort;

    @Schema(description = "状态（0正常 1停用）")
    @NotBlank(message = "状态不能为空")
    private String status;

    @Schema(description = "备注")
    @Size(max = 50, message = "备注不能超过500个字符")
    private String remark;

}

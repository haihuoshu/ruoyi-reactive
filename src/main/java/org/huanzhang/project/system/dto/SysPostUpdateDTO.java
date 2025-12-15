package org.huanzhang.project.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "SysPostUpdateDTO")
@Data
public class SysPostUpdateDTO implements Serializable {

    @Schema(description = "岗位ID")
    @NotNull(message = "岗位ID不能为空")
    private Long postId;

    @Schema(description = "岗位编码")
    @Size(max = 64, message = "岗位编码不能超过64个字符")
    private String postCode;

    @Schema(description = "岗位名称")
    @Size(max = 50, message = "岗位名称不能超过50个字符")
    private String postName;

    @Schema(description = "岗位排序")
    private Integer postSort;

    @Schema(description = "状态（0正常 1停用）")
    private String status;

    @Schema(description = "备注")
    @Size(max = 50, message = "备注不能超过500个字符")
    private String remark;

}

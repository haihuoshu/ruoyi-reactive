package org.huanzhang.project.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "SysDictInsertDTO")
@Data
public class SysDictInsertDTO implements Serializable {

    @Schema(description = "字典类型")
    @NotBlank(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型不能超过100个字符")
    private String dictType;

    @Schema(description = "字典值")
    @NotBlank(message = "字典值不能为空")
    @Size(max = 100, message = "字典值不能超过100个字符")
    private String dictValue;

    @Schema(description = "字典标签")
    @NotBlank(message = "字典标签不能为空")
    @Size(max = 100, message = "字典标签不能超过100个字符")
    private String dictLabel;

    @Schema(description = "字典排序")
    @NotNull(message = "字典排序不能为空")
    private Integer dictSort;

    @Schema(description = "样式属性（其他样式扩展）")
    @Size(max = 100, message = "样式属性不能超过100个字符")
    private String cssClass;

    @Schema(description = "表格字典样式")
    @Size(max = 100, message = "表格字典样式不能超过100个字符")
    private String listClass;

    @Schema(description = "是否默认（Y是 N否）")
    @NotBlank(message = "是否默认不能为空")
    private String isDefault;

    @Schema(description = "状态（0正常 1停用）")
    @NotBlank(message = "状态不能为空")
    private String status;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注不能超过500个字符")
    private String remark;

}

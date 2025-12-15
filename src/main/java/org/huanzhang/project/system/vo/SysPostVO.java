package org.huanzhang.project.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.huanzhang.framework.aspectj.lang.annotation.Excel;
import org.huanzhang.framework.aspectj.lang.annotation.Excel.ColumnType;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(description = "SysPostVO")
@Data
public class SysPostVO implements Serializable {

    @Schema(description = "岗位ID")
    @Excel(name = "岗位ID", cellType = ColumnType.NUMERIC)
    private Long postId;

    @Schema(description = "岗位编码")
    @Excel(name = "岗位编码")
    private String postCode;

    @Schema(description = "岗位名称")
    @Excel(name = "岗位名称")
    private String postName;

    @Schema(description = "岗位排序")
    @Excel(name = "岗位排序")
    private Integer postSort;

    @Schema(description = "状态（0正常 1停用）")
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    @Schema(description = "备注")
    @Excel(name = "备注")
    private String remark;

    @Schema(description = "创建时间")
    @Excel(name = "创建时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}

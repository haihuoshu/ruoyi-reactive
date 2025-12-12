package org.huanzhang.project.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.huanzhang.framework.aspectj.lang.annotation.Excel;
import org.huanzhang.framework.aspectj.lang.annotation.Excel.ColumnType;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(description = "SysDictVO")
@Data
public class SysDictVO implements Serializable {

    @Schema(description = "字典ID")
    @Excel(name = "字典ID", cellType = ColumnType.NUMERIC)
    private Long dictId;

    @Schema(description = "字典排序")
    @Excel(name = "字典排序", cellType = ColumnType.NUMERIC)
    private Long dictSort;

    @Schema(description = "字典标签")
    @Excel(name = "字典标签")
    private String dictLabel;

    @Schema(description = "字典键值")
    @Excel(name = "字典键值")
    private String dictValue;

    @Schema(description = "字典类型")
    @Excel(name = "字典类型")
    private String dictType;

    @Schema(description = "样式属性（其他样式扩展）")
    private String cssClass;

    @Schema(description = "表格字典样式")
    private String listClass;

    @Schema(description = "是否默认（Y是 N否）")
    @Excel(name = "是否默认", readConverterExp = "Y=是,N=否")
    private String isDefault;

    @Schema(description = "状态（0正常 1停用）")
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    @Schema(description = "创建时间")
    @Excel(name = "创建时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "备注")
    @Excel(name = "备注")
    private String remark;

}

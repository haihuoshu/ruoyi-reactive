package org.huanzhang.project.system.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.huanzhang.framework.web.domain.AbstractDataScope;

@Schema(description = "SysDeptQuery")
@Data
public class SysDeptQuery implements AbstractDataScope {

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "上级部门ID")
    private Long parentId;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "部门状态（0正常 1停用）")
    private String status;

    @Schema(description = "数据范围过滤")
    private String dataScope;

}

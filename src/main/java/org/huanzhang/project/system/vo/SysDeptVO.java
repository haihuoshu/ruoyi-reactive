package org.huanzhang.project.system.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.huanzhang.framework.web.tree.TreeNode;

import java.util.List;

@Schema(description = "SysDeptVO")
@Data
public class SysDeptVO implements TreeNode<SysDeptVO> {

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "父部门ID")
    private Long parentId;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "显示顺序")
    private Integer orderNum;

    @Schema(description = "负责人")
    private String leader;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "部门状态（0正常 1停用）")
    private String status;

    @Schema(description = "下级部门")
    private List<SysDeptVO> children;

    @JsonIgnore
    @Override
    public Object getId() {
        return deptId;
    }

}

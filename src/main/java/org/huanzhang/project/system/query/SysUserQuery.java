package org.huanzhang.project.system.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.huanzhang.framework.web.domain.AbstractDataScope;
import org.huanzhang.framework.web.domain.PageQuery;

import java.time.LocalDate;

@Schema(description = "SysUserQuery")
@Data
public class SysUserQuery extends PageQuery implements AbstractDataScope {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户账号")
    private String userName;

    @Schema(description = "手机号码")
    private String phonenumber;

    @Schema(description = "账号状态（0正常 1停用）")
    private String status;

    @Schema(description = "创建时间 start")
    private LocalDate startTime;

    @Schema(description = "创建时间 end")
    private LocalDate endTime;

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "数据范围过滤")
    private String dataScope;

}

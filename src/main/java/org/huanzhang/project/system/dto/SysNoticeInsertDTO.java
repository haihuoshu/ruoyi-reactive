package org.huanzhang.project.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.huanzhang.common.xss.Xss;

import java.io.Serializable;

@Schema(description = "SysNoticeInsertDTO")
@Data
public class SysNoticeInsertDTO implements Serializable {

    @Schema(description = "通告标题")
    @Xss(message = "通告标题不能包含脚本字符")
    @NotBlank(message = "通告标题不能为空")
    @Size(max = 50, message = "通告标题不能超过50个字符")
    private String noticeTitle;

    @Schema(description = "通告类型（1通知 2公告）")
    private String noticeType;

    @Schema(description = "通告内容")
    private String noticeContent;

    @Schema(description = "通告状态（0正常 1关闭）")
    private String status;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注不能超过500个字符")
    private String remark;

}

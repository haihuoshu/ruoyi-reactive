package org.huanzhang.project.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(description = "SysNoticeVO")
@Data
public class SysNoticeVO implements Serializable {

    @Schema(description = "通告ID")
    private Long noticeId;

    @Schema(description = "通告标题")
    private String noticeTitle;

    @Schema(description = "通告类型（1通知 2公告）")
    private String noticeType;

    @Schema(description = "通告内容")
    private String noticeContent;

    @Schema(description = "通告状态（0正常 1关闭）")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建人")
    private String createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}

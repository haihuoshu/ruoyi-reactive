package org.huanzhang.project.system.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.huanzhang.framework.web.domain.PageQuery;

@Schema(description = "SysNoticeQuery")
@Data
public class SysNoticeQuery extends PageQuery {

    @Schema(description = "通告标题")
    private String noticeTitle;

    @Schema(description = "通告类型（1通知 2公告）")
    private String noticeType;

    @Schema(description = "创建人")
    private String createBy;

}

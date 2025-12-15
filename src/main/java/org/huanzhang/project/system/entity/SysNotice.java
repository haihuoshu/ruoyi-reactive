package org.huanzhang.project.system.entity;

import lombok.Data;
import org.huanzhang.framework.r2dbc.entity.AbstractAuditable;

/**
 * 通告表 sys_notice
 *
 * @author haihuoshu
 * @version 2025-12-15
 */
@Data
public class SysNotice extends AbstractAuditable {

    /**
     * 通告ID
     */
    private Long noticeId;
    /**
     * 通告标题
     */
    private String noticeTitle;
    /**
     * 通告类型（1通知 2公告）
     */
    private String noticeType;
    /**
     * 通告内容
     */
    private String noticeContent;
    /**
     * 通告状态（0正常 1关闭）
     */
    private String status;
    /**
     * 备注
     */
    private String remark;

}

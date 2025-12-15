package org.huanzhang.project.system.entity;

import lombok.Data;
import org.huanzhang.framework.r2dbc.entity.AbstractAuditable;

/**
 * 岗位表 sys_post
 *
 * @author haihuoshu
 * @version 2025-12-15
 */
@Data
public class SysPost extends AbstractAuditable {

    /**
     * 岗位序号
     */
    private Long postId;
    /**
     * 岗位编码
     */
    private String postCode;
    /**
     * 岗位名称
     */
    private String postName;
    /**
     * 岗位排序
     */
    private Integer postSort;
    /**
     * 状态（0正常 1停用）
     */
    private String status;
    /**
     * 备注
     */
    private String remark;

}

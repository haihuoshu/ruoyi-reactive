package org.huanzhang.project.system.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户和岗位关联 sys_user_post
 *
 * @author haihuoshu
 * @version 2025-12-15
 */
@Data
public class SysUserPost implements Serializable {

    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 岗位ID
     */
    private Long postId;

}

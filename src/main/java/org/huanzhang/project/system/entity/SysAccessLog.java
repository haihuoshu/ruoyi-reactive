package org.huanzhang.project.system.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 访问日志表 sys_access_log
 *
 * @author haihuoshu
 * @version 2025-12-15
 */
@Data
public class SysAccessLog implements Serializable {

    /**
     * ID
     */
    private Long infoId;
    /**
     * 用户账号
     */
    private String userName;
    /**
     * 登录状态（0成功 1失败）
     */
    private String status;
    /**
     * 登录IP地址
     */
    private String ipaddr;
    /**
     * 登录地点
     */
    private String loginLocation;
    /**
     * 浏览器类型
     */
    private String browser;
    /**
     * 操作系统
     */
    private String os;
    /**
     * 提示消息
     */
    private String msg;
    /**
     * 访问时间
     */
    private LocalDateTime loginTime;

}
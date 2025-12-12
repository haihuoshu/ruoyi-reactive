package org.huanzhang.project.system.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 字典表 sys_dict
 *
 * @author haihuoshu
 * @version 2025-12-11
 */
@Data
public class SysDict implements Serializable {

    /**
     * 字典ID
     */
    private Long dictId;
    /**
     * 字典类型
     */
    private String dictType;
    /**
     * 字典值
     */
    private String dictValue;
    /**
     * 字典标签
     */
    private String dictLabel;
    /**
     * 字典排序
     */
    private Integer dictSort;
    /**
     * 样式属性（其他样式扩展）
     */
    private String cssClass;
    /**
     * 表格字典样式
     */
    private String listClass;
    /**
     * 是否默认（Y是 N否）
     */
    private String isDefault;
    /**
     * 状态（0正常 1停用）
     */
    private String status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建者
     */
    private String createBy;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新者
     */
    private String updateBy;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}


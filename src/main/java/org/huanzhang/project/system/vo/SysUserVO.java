package org.huanzhang.project.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.huanzhang.framework.aspectj.lang.annotation.Excel;
import org.huanzhang.framework.aspectj.lang.annotation.Excel.ColumnType;
import org.huanzhang.framework.aspectj.lang.annotation.Excel.Type;
import org.huanzhang.project.system.entity.SysRole;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Schema(description = "SysUserVO")
@Data
public class SysUserVO implements Serializable {

    @Schema(description = "用户ID")
    @Excel(name = "用户序号", type = Type.EXPORT, cellType = ColumnType.NUMERIC, prompt = "用户编号")
    private Long userId;

    @Schema(description = "部门ID")
    @Excel(name = "部门编号", type = Type.IMPORT)
    private Long deptId;

    @Schema(description = "用户账号")
    @Excel(name = "登录名称")
    private String userName;

    @Schema(description = "用户昵称")
    @Excel(name = "用户名称")
    private String nickName;

    @Schema(description = "用户邮箱")
    @Excel(name = "用户邮箱")
    private String email;

    @Schema(description = "手机号码")
    @Excel(name = "手机号码", cellType = ColumnType.TEXT)
    private String phonenumber;

    @Schema(description = "用户性别")
    @Excel(name = "用户性别", readConverterExp = "0=男,1=女,2=未知")
    private String sex;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "账号状态（0正常 1停用）")
    @Excel(name = "账号状态", readConverterExp = "0=正常,1=停用")
    private String status;

    @Schema(description = "删除标志（0代表存在 2代表删除）")
    private String delFlag;

    @Schema(description = "最后登录IP")
    @Excel(name = "最后登录IP", type = Type.EXPORT)
    private String loginIp;

    @Schema(description = "最后登录时间")
    @Excel(name = "最后登录时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss", type = Type.EXPORT)
    private Date loginDate;

    @Schema(description = "密码最后更新时间")
    private Date pwdUpdateDate;

    @Schema(description = "角色对象")
    private List<SysRole> roles;

    @Schema(description = "角色组")
    private Long[] roleIds;

    @Schema(description = "岗位组")
    private Long[] postIds;

    @Schema(description = "角色ID")
    private Long roleId;

}

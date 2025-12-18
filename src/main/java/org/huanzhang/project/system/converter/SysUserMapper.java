package org.huanzhang.project.system.converter;

import org.huanzhang.project.system.dto.SysUserInsertDTO;
import org.huanzhang.project.system.dto.SysUserUpdateDTO;
import org.huanzhang.project.system.entity.SysUser;
import org.huanzhang.project.system.vo.SysUserVO;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface SysUserMapper {

    SysUser toEntity(SysUserInsertDTO source);

    SysUser toEntity(SysUserUpdateDTO source);

    SysUserVO toVo(SysUser source);

}

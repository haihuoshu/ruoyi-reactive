package org.huanzhang.project.system.converter;

import org.huanzhang.project.system.dto.SysRoleInsertDTO;
import org.huanzhang.project.system.dto.SysRoleUpdateDTO;
import org.huanzhang.project.system.entity.SysRole;
import org.huanzhang.project.system.vo.SysRoleVO;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface SysRoleMapper {

    SysRole toEntity(SysRoleInsertDTO source);

    SysRole toEntity(SysRoleUpdateDTO source);

    SysRoleVO toVo(SysRole source);

}

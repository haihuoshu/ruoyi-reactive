package org.huanzhang.project.system.converter;

import org.huanzhang.project.system.dto.SysMenuInsertDTO;
import org.huanzhang.project.system.dto.SysMenuUpdateDTO;
import org.huanzhang.project.system.entity.SysMenu;
import org.huanzhang.project.system.vo.SysMenuVO;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface SysMenuMapper {

    SysMenu toEntity(SysMenuInsertDTO source);

    SysMenu toEntity(SysMenuUpdateDTO source);

    SysMenuVO toVo(SysMenu source);

}

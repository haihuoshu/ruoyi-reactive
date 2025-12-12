package org.huanzhang.project.system.converter;

import org.huanzhang.project.system.dto.SysDictInsertDTO;
import org.huanzhang.project.system.dto.SysDictUpdateDTO;
import org.huanzhang.project.system.entity.SysDict;
import org.huanzhang.project.system.vo.SysDictVO;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface SysDictMapper {

    SysDict toEntity(SysDictInsertDTO source);

    SysDict toEntity(SysDictUpdateDTO source);

    SysDictVO toVo(SysDict source);

}

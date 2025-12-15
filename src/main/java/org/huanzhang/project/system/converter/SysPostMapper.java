package org.huanzhang.project.system.converter;

import org.huanzhang.project.system.dto.SysPostInsertDTO;
import org.huanzhang.project.system.dto.SysPostUpdateDTO;
import org.huanzhang.project.system.entity.SysPost;
import org.huanzhang.project.system.vo.SysPostVO;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface SysPostMapper {

    SysPost toEntity(SysPostInsertDTO source);

    SysPost toEntity(SysPostUpdateDTO source);

    SysPostVO toVo(SysPost source);

}

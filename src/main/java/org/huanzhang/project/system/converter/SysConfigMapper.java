package org.huanzhang.project.system.converter;

import org.huanzhang.project.system.dto.SysConfigInsertDTO;
import org.huanzhang.project.system.dto.SysConfigUpdateDTO;
import org.huanzhang.project.system.entity.SysConfig;
import org.huanzhang.project.system.vo.SysConfigVO;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface SysConfigMapper {

    SysConfig toEntity(SysConfigInsertDTO source);

    SysConfig toEntity(SysConfigUpdateDTO source);

    SysConfigVO toVo(SysConfig source);

}

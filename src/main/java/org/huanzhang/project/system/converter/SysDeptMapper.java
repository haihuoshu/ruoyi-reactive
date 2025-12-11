package org.huanzhang.project.system.converter;

import org.huanzhang.project.system.dto.SysDeptInsertDTO;
import org.huanzhang.project.system.dto.SysDeptUpdateDTO;
import org.huanzhang.project.system.entity.SysDept;
import org.huanzhang.project.system.vo.SysDeptVO;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface SysDeptMapper {

    SysDept toEntity(SysDeptInsertDTO source);

    SysDept toEntity(SysDeptUpdateDTO source);

    SysDeptVO toVo(SysDept source);

}

package org.huanzhang.project.system.converter;

import org.huanzhang.project.system.dto.SysNoticeInsertDTO;
import org.huanzhang.project.system.dto.SysNoticeUpdateDTO;
import org.huanzhang.project.system.entity.SysNotice;
import org.huanzhang.project.system.vo.SysNoticeVO;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface SysNoticeMapper {

    SysNotice toEntity(SysNoticeInsertDTO source);

    SysNotice toEntity(SysNoticeUpdateDTO source);

    SysNoticeVO toVo(SysNotice source);

}

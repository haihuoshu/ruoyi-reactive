package org.huanzhang.project.system.converter;

import org.huanzhang.project.system.entity.SysAccessLog;
import org.huanzhang.project.system.vo.SysAccessLogVO;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface SysAccessLogMapper {

    SysAccessLogVO toVo(SysAccessLog source);

}

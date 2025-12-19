package org.huanzhang.project.system.converter;

import org.huanzhang.project.system.entity.SysOperateLog;
import org.huanzhang.project.system.vo.SysOperateLogVO;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface SysOperateLogMapper {

    SysOperateLogVO toVo(SysOperateLog source);

}

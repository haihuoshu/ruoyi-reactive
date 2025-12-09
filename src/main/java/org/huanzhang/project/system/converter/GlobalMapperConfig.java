package org.huanzhang.project.system.converter;

import org.mapstruct.MapperConfig;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
        // 所有映射器生成 Spring Bean
        componentModel = MappingConstants.ComponentModel.SPRING,
        // 未映射字段告警（而非报错）
        unmappedTargetPolicy = ReportingPolicy.WARN,
        // 源字段null则保留目标字段值
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface GlobalMapperConfig {
}

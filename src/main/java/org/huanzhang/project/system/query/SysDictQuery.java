package org.huanzhang.project.system.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.huanzhang.framework.web.domain.PageQuery;

@Schema(description = "SysDictQuery")
@Data
public class SysDictQuery extends PageQuery {

    @Schema(description = "字典类型")
    private String dictType;

    @Schema(description = "字典标签")
    private String dictLabel;

    @Schema(description = "字典值")
    private String dictValue;

    @Schema(description = "状态（0正常 1停用）")
    private String status;

}

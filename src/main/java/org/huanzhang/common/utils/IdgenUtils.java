package org.huanzhang.common.utils;

import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
import org.springframework.stereotype.Component;

/**
 * ID生成工具类
 *
 * @author haihuoshu
 * @version 2025-12-24
 */
@Component
public class IdgenUtils {

    static {
        IdGeneratorOptions options = new IdGeneratorOptions((short) 1);
        // 2025-01-01 00:00:00
        options.BaseTime = 1735660800000L;
        // 保存参数
        YitIdHelper.setIdGenerator(options);
    }

    public static long nextId() {
        return YitIdHelper.nextId();
    }

}

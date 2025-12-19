package org.huanzhang.framework.manager.factory;

import org.huanzhang.common.utils.ip.AddressUtils;
import org.huanzhang.common.utils.spring.SpringUtils;
import org.huanzhang.project.monitor.domain.SysOperLog;
import org.huanzhang.project.monitor.service.ISysOperLogService;

/**
 * 异步工厂（产生任务用）
 *
 * @author ruoyi
 */
public class AsyncFactory {

    /**
     * 操作日志记录
     *
     * @param operLog 操作日志信息
     */
    public static void recordOper(final SysOperLog operLog) {
        // 远程查询操作地点
        operLog.setOperLocation(AddressUtils.getRealAddressByIP(operLog.getOperIp()));
        SpringUtils.getBean(ISysOperLogService.class).insertOperlog(operLog);
    }
}

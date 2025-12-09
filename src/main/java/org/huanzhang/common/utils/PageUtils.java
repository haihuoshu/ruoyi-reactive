package org.huanzhang.common.utils;

import com.github.pagehelper.PageHelper;
import org.huanzhang.common.utils.sql.SqlUtil;
import org.huanzhang.framework.web.domain.PageQuery;
import org.huanzhang.framework.web.page.TableSupport;

/**
 * 分页工具类
 *
 * @author ruoyi
 */
public class PageUtils extends PageHelper {
    /**
     * 设置请求分页数据
     */
    public static void startPage() {
        PageQuery pageQuery = TableSupport.buildPageRequest();
        Integer pageNum = pageQuery.getPageNum();
        Integer pageSize = pageQuery.getPageSize();
        String orderBy = SqlUtil.escapeOrderBySql(pageQuery.getOrderBy());
        Boolean reasonable = pageQuery.getReasonable();
        //noinspection resource
        PageHelper.startPage(pageNum, pageSize, orderBy).setReasonable(reasonable);
    }

}

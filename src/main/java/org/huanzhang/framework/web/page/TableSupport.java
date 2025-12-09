package org.huanzhang.framework.web.page;

import org.huanzhang.common.core.text.Convert;
import org.huanzhang.common.utils.ServletUtils;
import org.huanzhang.framework.web.domain.PageQuery;

/**
 * 表格数据处理
 *
 * @author ruoyi
 */
public class TableSupport {
    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "pageNum";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 排序列
     */
    public static final String ORDER_BY_COLUMN = "orderByColumn";

    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    public static final String IS_ASC = "isAsc";

    /**
     * 分页参数合理化
     */
    public static final String REASONABLE = "reasonable";

    /**
     * 封装分页对象
     */
    public static PageQuery getPageDomain() {
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageNum(Convert.toInt(ServletUtils.getParameter(PAGE_NUM), 1));
        pageQuery.setPageSize(Convert.toInt(ServletUtils.getParameter(PAGE_SIZE), 10));
        pageQuery.setOrderByColumn(ServletUtils.getParameter(ORDER_BY_COLUMN));
        pageQuery.setIsAsc(ServletUtils.getParameter(IS_ASC));
        pageQuery.setReasonable(ServletUtils.getParameterToBool(REASONABLE));
        return pageQuery;
    }

    public static PageQuery buildPageRequest() {
        return getPageDomain();
    }
}

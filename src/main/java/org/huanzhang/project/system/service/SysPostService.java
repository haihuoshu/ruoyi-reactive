package org.huanzhang.project.system.service;

import org.huanzhang.project.system.dto.SysPostInsertDTO;
import org.huanzhang.project.system.dto.SysPostUpdateDTO;
import org.huanzhang.project.system.query.SysPostQuery;
import org.huanzhang.project.system.vo.SysPostVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 岗位表 业务层
 *
 * @author haihuoshu
 * @version 2025-12-15
 */
public interface SysPostService {

    /**
     * 根据条件查询岗位总数
     */
    Mono<Long> selectPostCountByQuery(SysPostQuery query);

    /**
     * 根据条件查询岗位列表
     */
    Flux<SysPostVO> selectPostListByQuery(SysPostQuery query);

    /**
     * 根据岗位ID查询详细信息
     */
    Mono<SysPostVO> selectPostById(Long postId);

    /**
     * 新增岗位
     */
    Mono<Void> insertPost(SysPostInsertDTO dto);

    /**
     * 修改岗位
     */
    Mono<Void> updatePost(SysPostUpdateDTO dto);

    /**
     * 批量删除岗位
     */
    Mono<Void> deletePostByIds(List<Long> postIds);

    /**
     * 查询所有岗位
     */
    Flux<SysPostVO> selectPostAll();
}

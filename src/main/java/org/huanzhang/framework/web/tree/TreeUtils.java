package org.huanzhang.framework.web.tree;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

/**
 * 树形结构工具类：用于将扁平结构的Treeable实现类集合转换为树形结构
 * 核心功能：通过ID与父ID的关联关系，递归构建层级结构
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE) // 工具类禁止实例化
public class TreeUtils {

    /**
     * 将扁平列表转换为树形结构
     */
    public static <T extends TreeNode<T>> List<T> getTree(List<T> list) {
        // 空集合直接返回空列表，避免后续空指针
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        // 1. 使用 Map 存储所有节点，方便通过 ID 快速查找
        Map<Object, T> nodeMap = new HashMap<>(list.size());
        for (T node : list) {
            nodeMap.put(node.getId(), node);
        }

        // 2. 遍历列表，将子节点挂载到父节点下
        List<T> rootNodes = new ArrayList<>();
        for (T node : list) {
            // 尝试从 Map 中获取当前节点的父节点
            T parent = nodeMap.get(node.getParentId());

            if (Objects.nonNull(parent)) {
                // 如果找到了父节点，将当前节点挂载到父节点下
                if (Objects.isNull(parent.getChildren())) {
                    parent.setChildren(new ArrayList<>());
                }
                parent.getChildren().add(node);
            } else {
                // 如果没找到父节点，说明是根节点
                rootNodes.add(node);
            }
        }

        return rootNodes;
    }

}

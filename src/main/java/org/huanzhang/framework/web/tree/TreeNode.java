package org.huanzhang.framework.web.tree;

import java.io.Serializable;
import java.util.List;

public interface TreeNode<T> extends Serializable {

    Object getId();

    Object getParentId();

    void setChildren(List<T> children);

    List<T> getChildren();

}

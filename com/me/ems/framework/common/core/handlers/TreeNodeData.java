package com.me.ems.framework.common.core.handlers;

import com.me.ems.framework.common.core.TreeNodeGroup;
import java.util.List;
import com.me.ems.framework.common.core.TreeNode;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Map;

public interface TreeNodeData
{
    TreeNode getNodeData(final Long p0, final Map p1, final MultivaluedMap<String, String> p2) throws Exception;
    
    TreeNode getRootNode(final Long p0, final Map p1, final MultivaluedMap<String, String> p2) throws Exception;
    
    List<TreeNode> getChildTreeNodes(final Long p0, final Map p1, final MultivaluedMap<String, String> p2) throws Exception;
    
    TreeNodeGroup getTreeNodeObject(final Long p0, final Map p1, final MultivaluedMap<String, String> p2) throws Exception;
    
    default int getTotalNumberOfResources(final Long treeID, final Map userParams, final MultivaluedMap<String, String> requestInput) throws Exception {
        return 0;
    }
}

package com.me.ems.framework.common.core.handlers;

import com.me.ems.framework.common.core.TreeNodeGroup;
import java.util.List;
import com.me.ems.framework.common.core.TreeNode;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Map;

public class DefaultListViewHandler implements TreeNodeData
{
    @Override
    public TreeNode getNodeData(final Long treeID, final Map userParams, final MultivaluedMap<String, String> requestInput) throws Exception {
        return null;
    }
    
    @Override
    public TreeNode getRootNode(final Long treeID, final Map userParams, final MultivaluedMap<String, String> requestInput) throws Exception {
        return null;
    }
    
    @Override
    public List<TreeNode> getChildTreeNodes(final Long treeID, final Map userParams, final MultivaluedMap<String, String> requestInput) throws Exception {
        return null;
    }
    
    @Override
    public TreeNodeGroup getTreeNodeObject(final Long treeID, final Map userParams, final MultivaluedMap<String, String> requestInput) throws Exception {
        final TreeNodeGroup nodeGroup = new TreeNodeGroup();
        nodeGroup.setTreeID(treeID);
        nodeGroup.setTotalCount(this.getTotalNumberOfResources(treeID, userParams, requestInput));
        nodeGroup.setParent(null);
        nodeGroup.setResources(this.getChildTreeNodes(treeID, userParams, requestInput));
        nodeGroup.addProperty("canTraverse", false);
        return nodeGroup;
    }
    
    @Override
    public int getTotalNumberOfResources(final Long treeID, final Map userParams, final MultivaluedMap<String, String> requestInput) throws Exception {
        return 0;
    }
}

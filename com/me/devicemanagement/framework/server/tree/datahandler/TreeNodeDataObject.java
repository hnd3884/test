package com.me.devicemanagement.framework.server.tree.datahandler;

import org.json.JSONObject;
import java.util.List;
import com.me.devicemanagement.framework.server.tree.TreeNode;
import com.me.devicemanagement.framework.server.tree.NodeSettings;

public interface TreeNodeDataObject
{
    TreeNode getRootNode(final NodeSettings p0) throws Exception;
    
    List getChildTreeNodes(final NodeSettings p0) throws Exception;
    
    JSONObject getJSONObject(final NodeSettings p0) throws Exception;
}

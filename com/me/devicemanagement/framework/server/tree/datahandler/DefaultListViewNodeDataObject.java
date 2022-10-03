package com.me.devicemanagement.framework.server.tree.datahandler;

import java.util.Map;
import com.me.devicemanagement.framework.server.tree.TreeNode;
import java.util.List;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.tree.NodeSettings;

public abstract class DefaultListViewNodeDataObject implements TreeNodeDataObject
{
    @Override
    public JSONObject getJSONObject(final NodeSettings nodeSettings) throws Exception {
        final List childNodesList = this.getChildTreeNodes(nodeSettings);
        JSONObject jsonObject = null;
        final JSONDataHandler handler = new JSONDataHandler();
        jsonObject = handler.createJSONObject("0", childNodesList);
        return jsonObject;
    }
    
    @Override
    public TreeNode getRootNode(final NodeSettings nodeSettings) throws Exception {
        return null;
    }
    
    public NodeSettings getNodeSettings(final Map hMap) throws Exception {
        final NodeSettings nodeSettings = null;
        int treeId = -1;
        if (hMap.isEmpty()) {
            return nodeSettings;
        }
        final String temp_treeTypeID = hMap.get("treeId");
        if (temp_treeTypeID != null && !temp_treeTypeID.equalsIgnoreCase("")) {
            treeId = Integer.parseInt(temp_treeTypeID);
        }
        nodeSettings.treeId = treeId;
        nodeSettings.isTreeView = false;
        nodeSettings.isTraverse = hMap.get("is_traverse");
        nodeSettings.userData = hMap;
        nodeSettings.parent_id = hMap.get("parent_id");
        return nodeSettings;
    }
}

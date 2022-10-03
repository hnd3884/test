package com.me.devicemanagement.framework.server.tree;

import java.util.Map;

public class NodeSettings
{
    public int treeId;
    public boolean isTreeView;
    public Map userData;
    public String isTraverse;
    public String parent_id;
    
    public NodeSettings() {
        this.treeId = -1;
        this.isTreeView = true;
        this.userData = null;
        this.isTraverse = null;
        this.parent_id = null;
    }
}

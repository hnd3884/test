package com.me.devicemanagement.framework.server.tree;

public class TreeMetaData
{
    public String treeName;
    public int treeId;
    public String treeDataHandlerName;
    public String loggerName;
    
    public TreeMetaData() {
        this.treeName = null;
        this.treeId = -1;
        this.treeDataHandlerName = null;
        this.loggerName = null;
    }
    
    @Override
    public String toString() {
        return "treeName=" + this.treeName + ";treeId=" + this.treeId + "; treeDataHandlerName=" + this.treeDataHandlerName + "; loggerName=" + this.loggerName;
    }
}

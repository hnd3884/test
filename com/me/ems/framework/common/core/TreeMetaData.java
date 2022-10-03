package com.me.ems.framework.common.core;

public class TreeMetaData
{
    private Long treeID;
    private String treeName;
    private String dataHandlerName;
    private String loggerName;
    
    public TreeMetaData() {
    }
    
    public TreeMetaData(final Long treeID, final String treeName, final String dataHandlerName, final String loggerName) {
        this.treeID = treeID;
        this.treeName = treeName;
        this.dataHandlerName = dataHandlerName;
        this.loggerName = loggerName;
    }
    
    public Long getTreeID() {
        return this.treeID;
    }
    
    public void setTreeID(final Long treeID) {
        this.treeID = treeID;
    }
    
    public String getTreeName() {
        return this.treeName;
    }
    
    public void setTreeName(final String treeName) {
        this.treeName = treeName;
    }
    
    public String getDataHandlerName() {
        return this.dataHandlerName;
    }
    
    public void setDataHandlerName(final String dataHandlerName) {
        this.dataHandlerName = dataHandlerName;
    }
    
    public String getLoggerName() {
        return this.loggerName;
    }
    
    public void setLoggerName(final String loggerName) {
        this.loggerName = loggerName;
    }
    
    @Override
    public String toString() {
        return "[treeName=" + this.treeName + "; treeId=" + this.treeID + "; treeDataHandlerName=" + this.dataHandlerName + "; loggerName=" + this.loggerName + "]";
    }
}

package com.me.ems.framework.common.core;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class TreeNodeGroup
{
    private Long treeID;
    private List<TreeNode> resources;
    private int totalCount;
    private TreeNode parent;
    private Map<String, Object> properties;
    
    public TreeNodeGroup() {
        this.resources = new ArrayList<TreeNode>();
        this.properties = new HashMap<String, Object>();
    }
    
    public TreeNodeGroup(final Long treeID, final Integer totalCount, final TreeNode parent) {
        this.resources = new ArrayList<TreeNode>();
        this.properties = new HashMap<String, Object>();
        this.treeID = treeID;
        this.totalCount = totalCount;
        this.parent = parent;
    }
    
    public Long getTreeID() {
        return this.treeID;
    }
    
    public void setTreeID(final Long treeID) {
        this.treeID = treeID;
    }
    
    public List<TreeNode> getResources() {
        return this.resources;
    }
    
    public void setResources(final List<TreeNode> resources) {
        this.resources = resources;
    }
    
    public void addResource(final TreeNode treeNode) {
        this.resources.add(treeNode);
    }
    
    public void removeResource(final TreeNode treeNode) {
        this.resources.remove(treeNode);
    }
    
    public int getTotalCount() {
        return this.totalCount;
    }
    
    public void setTotalCount(final int totalCount) {
        this.totalCount = totalCount;
    }
    
    public TreeNode getParent() {
        return this.parent;
    }
    
    public void setParent(final TreeNode parent) {
        this.parent = parent;
    }
    
    public Map<String, Object> getProperties() {
        return this.properties;
    }
    
    public void setProperties(final Map<String, Object> properties) {
        this.properties = properties;
    }
    
    public void addProperty(final String key, final Object value) {
        this.properties.put(key, value);
    }
    
    public void removeProperty(final String key) {
        this.properties.remove(key);
    }
}

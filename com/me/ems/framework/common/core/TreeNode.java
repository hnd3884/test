package com.me.ems.framework.common.core;

import java.util.HashMap;
import java.util.Map;

public class TreeNode
{
    public String id;
    public String parentID;
    public String name;
    public String displayName;
    public Boolean hasChild;
    public Map<String, Object> properties;
    
    public TreeNode() {
        this.hasChild = false;
        this.properties = new HashMap<String, Object>();
    }
    
    public TreeNode(final String id, final String parentID, final String name) {
        this(id, parentID, name, name, false);
    }
    
    public TreeNode(final String id, final String parentID, final String name, final String displayName, final boolean hasChild) {
        this.hasChild = false;
        this.properties = new HashMap<String, Object>();
        this.id = id;
        this.parentID = parentID;
        this.name = name;
        this.displayName = displayName;
        this.hasChild = hasChild;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    public String getParentID() {
        return this.parentID;
    }
    
    public void setParentID(final String parentID) {
        this.parentID = parentID;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }
    
    public Boolean getHasChild() {
        return this.hasChild;
    }
    
    public void setHasChild(final Boolean hasChild) {
        this.hasChild = hasChild;
    }
    
    public Map<String, Object> getProperties() {
        return this.properties;
    }
    
    public Object getProperty(final String key) {
        return this.properties.get(key);
    }
    
    public void addProperty(final String key, final Object value) {
        this.properties.put(key, value);
    }
    
    public void removeProperty(final String key) {
        this.properties.remove(key);
    }
}

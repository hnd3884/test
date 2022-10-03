package com.me.ems.framework.common.api.v1.model;

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node<T>
{
    private T id;
    private T parent;
    private String label;
    private Map properties;
    private List<Node<T>> children;
    
    public T getId() {
        return this.id;
    }
    
    public void setId(final T id) {
        this.id = id;
    }
    
    public T getParent() {
        return this.parent;
    }
    
    public void setParent(final T parent) {
        this.parent = parent;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public void setLabel(final String label) {
        this.label = label;
    }
    
    public Map getProperties() {
        return this.properties;
    }
    
    public void setProperties(final Map properties) {
        this.properties = new HashMap(properties);
    }
    
    public List<Node<T>> getChildren() {
        return this.children;
    }
    
    public void setChildren(final List<Node<T>> children) {
        this.children = new ArrayList<Node<T>>(children);
    }
    
    public void addChild(final Node<T> child) {
        this.children = ((this.children == null) ? new ArrayList<Node<T>>() : this.children);
        child.parent = this.id;
        this.children.add(child);
    }
    
    public void addProperty(final Object key, final Object value) {
        (this.properties = ((this.properties == null) ? new HashMap() : this.properties)).put(key, value);
    }
    
    public boolean isRoot() {
        return this.parent == null;
    }
    
    public boolean isLeaf() {
        return this.children == null;
    }
}

package com.me.ems.onpremise.productbanner.api.v1.model;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Node<T>
{
    private T id;
    private Node<T> parent;
    private Map properties;
    private List<Node<T>> children;
    
    public Node(final T id) {
        this.setId(id);
    }
    
    public T getId() {
        return this.id;
    }
    
    public void setId(final T id) {
        this.id = id;
    }
    
    public Node<T> getParent() {
        return this.parent;
    }
    
    private void setParent(final Node<T> parent) {
        this.parent = parent;
    }
    
    public Map getProperties() {
        return Collections.unmodifiableMap((Map<?, ?>)this.properties);
    }
    
    public List<Node<T>> getChildren() {
        return Collections.unmodifiableList((List<? extends Node<T>>)this.children);
    }
    
    public void addChild(final Node<T> child) {
        this.children = ((this.children == null) ? new ArrayList<Node<T>>() : this.children);
        if (!this.children.contains(child)) {
            child.setParent(this);
            this.children.add(child);
        }
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
    
    @Override
    public boolean equals(final Object obj) {
        final Node<T> node = (Node<T>)obj;
        return (node == null) ? Boolean.FALSE : node.getId().equals(this.getId());
    }
    
    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }
}

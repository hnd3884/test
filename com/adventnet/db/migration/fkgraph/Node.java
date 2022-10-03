package com.adventnet.db.migration.fkgraph;

import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class Node
{
    private String nodeName;
    private boolean isRootNode;
    private List<Node> childNodes;
    private List<Node> parentNodes;
    private int level;
    
    public Node(final String nodeName) {
        this.isRootNode = Boolean.TRUE;
        this.childNodes = new ArrayList<Node>();
        this.parentNodes = new ArrayList<Node>();
        this.level = -1;
        this.setNodeName(nodeName);
    }
    
    public String getNodeName() {
        return this.nodeName;
    }
    
    public void setNodeName(final String nodeName) {
        this.nodeName = nodeName;
    }
    
    public boolean isRootNode() {
        return this.isRootNode;
    }
    
    private void setRootNode(final boolean isRootNode) {
        this.isRootNode = isRootNode;
    }
    
    public void addParent(final Node parentNode) {
        if (!this.parentNodes.contains(parentNode)) {
            this.setRootNode(false);
            this.parentNodes.add(parentNode);
        }
    }
    
    public void addChild(final Node childNode) {
        if (!this.childNodes.contains(childNode)) {
            this.childNodes.add(childNode);
        }
    }
    
    public List<Node> getAllParentNodes() {
        return Collections.unmodifiableList((List<? extends Node>)this.parentNodes);
    }
    
    public List<Node> getAllChildNodes() {
        return Collections.unmodifiableList((List<? extends Node>)this.childNodes);
    }
    
    public boolean hasChild(final Node childNode) {
        return this.isRootNode;
    }
    
    public boolean containsChild(final Node childNode) {
        return this.getAllChildNodes().contains(childNode);
    }
    
    @Override
    public boolean equals(final Object obj) {
        final Node node = (Node)obj;
        return (node == null) ? Boolean.FALSE : node.getNodeName().equals(this.nodeName);
    }
    
    @Override
    public int hashCode() {
        return this.getNodeName().hashCode();
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public void setLevel(final int level) {
        if (this.level == -1 || this.level < level) {
            this.level = level;
        }
        else if (this.level >= level) {
            return;
        }
        for (final Node node : this.childNodes) {
            node.setLevel(this.getLevel() + 1);
        }
    }
}
